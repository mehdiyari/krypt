package ir.mehdiyari.krypt.backup.logic.backup

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.runs
import io.mockk.verify
import ir.mehdiyari.krypt.account.api.UserKeyProvider
import ir.mehdiyari.krypt.account.api.UsernameProvider
import ir.mehdiyari.krypt.accounts.data.dao.AccountsDao
import ir.mehdiyari.krypt.accounts.data.entity.AccountEntity
import ir.mehdiyari.krypt.backup.data.dao.BackupDao
import ir.mehdiyari.krypt.backup.data.entity.BackupEntity
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.cryptography.utils.Base64Wrapper
import ir.mehdiyari.krypt.cryptography.utils.HashingUtils
import ir.mehdiyari.krypt.cryptography.utils.SymmetricHelper
import ir.mehdiyari.krypt.cryptography.utils.getBytesBetweenIndexes
import ir.mehdiyari.krypt.file.data.dao.FilesDao
import ir.mehdiyari.krypt.file.data.entity.FileEntity
import ir.mehdiyari.krypt.file.data.entity.FileTypeEnum
import ir.mehdiyari.krypt.files.logic.utils.FileWrapper
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.security.Key
import java.text.SimpleDateFormat
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey

@OptIn(ExperimentalCoroutinesApi::class)
internal class BackupRepositoryTest {

    private lateinit var accountsDao: AccountsDao
    private lateinit var filesDao: FilesDao
    private lateinit var backupDao: BackupDao
    private lateinit var usernameProvider: UsernameProvider
    private lateinit var dbBackupModelJsonAdapter: DBBackupModelJsonAdapter
    private lateinit var fileUtils: FilesUtilities
    private lateinit var kryptCryptographyHelper: KryptCryptographyHelper
    private lateinit var symmetricHelper: SymmetricHelper
    private lateinit var userKeyProvider: UserKeyProvider
    private lateinit var base64Wrapper: Base64Wrapper
    private lateinit var fileWrapper: FileWrapper

    private lateinit var backupRepository: BackupRepository

    @Before
    fun setup() {
        accountsDao = mockk()
        filesDao = mockk()
        backupDao = mockk()
        usernameProvider = mockk()
        dbBackupModelJsonAdapter = mockk()
        fileUtils = mockk()
        kryptCryptographyHelper = mockk()
        symmetricHelper = mockk()
        userKeyProvider = mockk()
        base64Wrapper = mockk()
        fileWrapper = mockk()

        backupRepository = BackupRepositoryImpl(
            accountsDao,
            filesDao,
            backupDao,
            usernameProvider,
            dbBackupModelJsonAdapter,
            fileUtils,
            kryptCryptographyHelper,
            symmetricHelper,
            userKeyProvider,
            base64Wrapper,
            fileWrapper
        )
    }

    @Ignore("the function is so complex and have side effects(writing on a file)")
    @Test
    fun `backupAll creates a backup and adds it to the backup table`() = runTest {
        val username = "testUser"
        val encryptedName = "testEncryptedName"
        val user = AccountEntity(username, encryptedName)
        val salt = ByteArray(16)
        val files = listOf(
            FileEntity(
                id = 0,
                type = null,
                filePath = "path",
                metaData = "meta",
                accountName = username,
            ),
            FileEntity(
                id = 0,
                type = FileTypeEnum.Photo,
                filePath = "path1",
                metaData = "meta1",
                accountName = username,
            )
        )
        val backupFilePath = "testBackupPath"
        val backupFile = mockk<File>(backupFilePath)
        val cipher = mockk<Cipher>()
        val initVector = ByteArray(16)
        val key = mockk<SecretKey>()
        val dbEncryptedBytes = ByteArray(32)

        every { usernameProvider.getUsername() } returns username
        coEvery { accountsDao.getAccountWithName(username) } returns user

        val encryptedNameSize = SymmetricHelper.INITIALIZE_VECTOR_SIZE + HashingUtils.SALT_SIZE
        val fakeDecodedName = ByteArray(encryptedNameSize)
        every { base64Wrapper.decode(encryptedName) } returns fakeDecodedName
        every { base64Wrapper.decode(any()).getBytesBetweenIndexes(any(), any()) } returns salt

        coEvery { filesDao.getAllFiles(username) } returns files
        every { fileUtils.generateBackupFilePath(username) } returns backupFilePath
        every { symmetricHelper.getAESCipher() } returns cipher
        every { symmetricHelper.createInitVector() } returns initVector
        every { userKeyProvider.getKey() } returns key
        coEvery { kryptCryptographyHelper.encryptBytes(any(), any()) } returns Result.success(
            dbEncryptedBytes
        )
        every { cipher.init(any(), any() as Key) } just Runs
        every { backupFile.exists() } returns true
        coEvery { backupDao.insert(any()) } just Runs

        mockkConstructor(FileOutputStream::class)
        every { anyConstructed<FileOutputStream>().write(any<ByteArray>()) } just Runs
        mockkConstructor(CipherOutputStream::class)
        every { anyConstructed<CipherOutputStream>().write(any<ByteArray>()) } just Runs
        val result = backupRepository.backupAll()

        assertTrue(result)

        // TODO: Mehdi please take care of this
    }

    @Test
    fun `getBackupRecord returns expected records`() = runTest {
        val username = "username"
        val expected = listOf(
            BackupEntity(
                id = 1,
                filePath = "path",
                dateTime = System.currentTimeMillis(),
                account = username
            )
        )
        coEvery { usernameProvider.getUsername() } returns username
        coEvery { backupDao.getAllBackups(username) } returns expected

        val actual = backupRepository.getBackupRecord()

        assertEquals(expected, actual)
        coVerify(exactly = 1) { backupDao.getAllBackups(username) }
    }

    @Test
    fun `getLastBackUpDateTime returns expected date time`() = runTest {
        val username = "username"
        val expectedDateTime = System.currentTimeMillis()
        val expectedDateFormat =
            SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(expectedDateTime)

        coEvery { usernameProvider.getUsername() } returns username
        coEvery { backupDao.getLastBackupRecord(username) } returns expectedDateTime

        val actualDateTime = backupRepository.getLastBackUpDateTime()

        assertEquals(expectedDateFormat, actualDateTime)
        coVerify(exactly = 1) { backupDao.getLastBackupRecord(username) }
    }

    @Test
    fun `getLastBackUpDateTime returns empty string if no date time available`() = runTest {
        val username = "username"

        coEvery { usernameProvider.getUsername() } returns username
        coEvery { backupDao.getLastBackupRecord(username) } returns null

        val actualDateTime = backupRepository.getLastBackUpDateTime()

        assertEquals("", actualDateTime)
        coVerify(exactly = 1) { backupDao.getLastBackupRecord(username) }
    }

    @Test
    fun `convertToBackUpDateTimeFormat returns expected date format`() {
        val expectedDateTime = System.currentTimeMillis()
        val expectedDateFormat =
            SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(expectedDateTime)

        val actualDateFormat = backupRepository.convertToBackUpDateTimeFormat(expectedDateTime)

        assertEquals(expectedDateFormat, actualDateFormat)
    }

    @Test
    fun `deleteBackupWithId removes file and deletes backup record`() = runTest {
        val backupFileId = 1
        val username = "username"
        val filePath = "filePath"
        val backupEntity = BackupEntity(backupFileId, filePath, 123456, username)

        every { usernameProvider.getUsername() } returns username
        coEvery { backupDao.getEntityWithId(backupFileId, username) } returns backupEntity
        coEvery {
            backupDao.deleteBackupWithId(backupFileId, username)
        } just runs
        every { fileWrapper.delete(filePath) } returns true

        backupRepository.deleteBackupWithId(backupFileId)

        coVerify(exactly = 1) {
            backupDao.getEntityWithId(
                backupFileId, username
            )
        }
        coVerify(exactly = 1) { backupDao.deleteBackupWithId(backupFileId, username) }
        verify(exactly = 1) { fileWrapper.delete(filePath) }
    }

    @Test
    fun `getBackupFilePathWithId returns the correct file path`() = runTest {
        val backupFileId = 1
        val username = "username"
        val expectedFilePath = "expectedFilePath"
        val backupEntity = BackupEntity(backupFileId, expectedFilePath, 123456, username)

        every { usernameProvider.getUsername() } returns username
        coEvery { backupDao.getEntityWithId(backupFileId, username) } returns backupEntity

        val actualFilePath = backupRepository.getBackupFilePathWithId(backupFileId)

        assertEquals(expectedFilePath, actualFilePath)
    }

    @Test
    fun `deleteCachedBackupFiles deletes all files`() = runTest {
        val username = "username"
        val files = listOf("filePath1", "filePath2")

        every { usernameProvider.getUsername() } returns username
        coEvery { backupDao.getAllBackupFiles(username) } returns files

        every { fileWrapper.delete(any()) } returns true

        backupRepository.deleteCachedBackupFiles()

        verify(exactly = files.size) { fileWrapper.delete(any()) }
        coVerify(exactly = 1) { backupDao.getAllBackupFiles(username) }
    }
}
