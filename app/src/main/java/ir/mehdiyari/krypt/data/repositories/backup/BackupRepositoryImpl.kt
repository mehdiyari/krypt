package ir.mehdiyari.krypt.data.repositories.backup

import ir.mehdiyari.krypt.account.api.UserKeyProvider
import ir.mehdiyari.krypt.account.api.UsernameProvider
import ir.mehdiyari.krypt.accounts.data.dao.AccountsDao
import ir.mehdiyari.krypt.accounts.data.entity.AccountEntity
import ir.mehdiyari.krypt.backup.data.dao.BackupDao
import ir.mehdiyari.krypt.backup.data.entity.BackupEntity
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.cryptography.utils.HashingUtils
import ir.mehdiyari.krypt.cryptography.utils.SymmetricHelper
import ir.mehdiyari.krypt.cryptography.utils.combineWith
import ir.mehdiyari.krypt.cryptography.utils.getBestBufferSizeForFile
import ir.mehdiyari.krypt.cryptography.utils.getBytesBetweenIndexes
import ir.mehdiyari.krypt.cryptography.utils.toByteArray
import ir.mehdiyari.krypt.cryptography.utils.toUtf8Bytes
import ir.mehdiyari.krypt.data.repositories.Base64Wrapper
import ir.mehdiyari.krypt.data.repositories.files.FileWrapper
import ir.mehdiyari.krypt.file.data.dao.FilesDao
import ir.mehdiyari.krypt.file.data.entity.FileEntity
import ir.mehdiyari.krypt.utils.FilesUtilities
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val accountsDao: AccountsDao,
    private val filesDao: FilesDao,
    private val backupDao: BackupDao,
    private val usernameProvider: UsernameProvider,
    private val dbBackupModelJsonAdapter: DBBackupModelJsonAdapter,
    private val fileUtils: FilesUtilities,
    private val kryptCryptographyHelper: KryptCryptographyHelper,
    private val symmetricHelper: SymmetricHelper,
    private val userKeyProvider: UserKeyProvider,
    private val base64Wrapper: Base64Wrapper,
    private val fileWrapper: FileWrapper
) : BackupRepository {

    /**
     *  Backup from all data's in db with all encrypted files.
     *
     *  Output File Schema:
     *  [Salt[^IV[EN_PART:[^DB_SIZE, ^EN_DB, ^F_SIZE, ^EN_F_CONTENT, ^F_SIZE, ^EN_F_CONTENT]]]]
     */
    override suspend fun backupAll(): Boolean {
        val user = accountsDao.getAccountWithName(usernameProvider.getUsername()!!)!!
        val salt =
            base64Wrapper.decode(user.encryptedName)
                .let { name ->
                    name.getBytesBetweenIndexes(
                        start = name.size - (SymmetricHelper.INITIALIZE_VECTOR_SIZE + HashingUtils.SALT_SIZE),
                        end = name.size - SymmetricHelper.INITIALIZE_VECTOR_SIZE
                    )
                }

        val files = filesDao.getAllFiles(usernameProvider.getUsername()!!).filter {
            File(it.filePath).exists()
        }

        val backupFilePath = fileUtils.generateBackupFilePath(usernameProvider.getUsername()!!)
        val backupFile = File(backupFilePath)

        val (cipher, initVector) = symmetricHelper.getAESCipher() to symmetricHelper.createInitVector()
        cipher.init(Cipher.ENCRYPT_MODE, userKeyProvider.getKey(), IvParameterSpec(initVector))

        FileOutputStream(backupFile).use { backupStream ->

            // write salt
            backupStream.write(salt)

            // write init vector to raw stream
            backupStream.write(initVector)

            // create cipher stream in top of normal stream
            CipherOutputStream(backupStream, cipher).use { encryptedBackupStream ->

                val encryptInitVector = symmetricHelper.createInitVector()
                val dbEncryptedBytes =
                    encryptInitVector.combineWith(
                        kryptCryptographyHelper.encryptBytes(
                            createDBBackupAsBytes(user, files),
                            encryptInitVector
                        ).getOrThrow()
                    )

                // write size and encrypted content of db backup to [encryptedBackupStream]
                encryptedBackupStream.write(dbEncryptedBytes.size.toLong().toByteArray())
                encryptedBackupStream.write(dbEncryptedBytes)

                // write files id, size and content to [encryptedBackupStream]
                writeFilesToEncryptedStream(files, encryptedBackupStream)
            }
        }

        // insert backup record to backup table
        backupDao.insert(
            BackupEntity(
                filePath = backupFilePath,
                dateTime = System.currentTimeMillis(),
                account = usernameProvider.getUsername()!!
            )
        )

        return backupFile.exists()
    }

    private fun writeFilesToEncryptedStream(
        files: List<FileEntity>,
        encryptedBackupStream: CipherOutputStream
    ) {
        files.forEach { currentFileEntity ->
            File(currentFileEntity.filePath).also {
                val size = it.length()
                val id = currentFileEntity.id
                encryptedBackupStream.write(size.toByteArray())
                encryptedBackupStream.write(id.toByteArray())

                val bestBufferSize = getBestBufferSizeForFile(size)
                FileInputStream(it).use { currentFileStream ->
                    while (true) {
                        val buffer = ByteArray(bestBufferSize)
                        val count = currentFileStream.read(buffer)
                        if (count == -1) {
                            break
                        } else {
                            encryptedBackupStream.write(buffer)
                        }
                    }
                }

            }
        }
    }

    private fun createDBBackupAsBytes(
        user: AccountEntity,
        files: List<FileEntity>
    ): ByteArray {
        return dbBackupModelJsonAdapter.toJson(DBBackupModel(user, files))!!.toUtf8Bytes()
    }

    override suspend fun getBackupRecord(): List<BackupEntity> = backupDao.getAllBackups(
        usernameProvider.getUsername()!!
    )

    override suspend fun getLastBackUpDateTime(): String {
        val dateTime = backupDao.getLastBackupRecord(usernameProvider.getUsername()!!)
        return if (dateTime == null) {
            ""
        } else {
            convertToBackUpDateTimeFormat(dateTime)
        }
    }

    override fun convertToBackUpDateTimeFormat(dateTime: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        return try {
            simpleDateFormat.format(dateTime)
        } catch (t: Throwable) {
            ""
        }
    }

    override suspend fun deleteBackupWithId(backupFileId: Int) {
        backupDao.getEntityWithId(
            backupFileId, usernameProvider.getUsername()!!
        )!!.also {
            fileWrapper.delete(it.filePath)
            backupDao.deleteBackupWithId(backupFileId, usernameProvider.getUsername()!!)
        }
    }

    override suspend fun getBackupFilePathWithId(backupFileId: Int): String {
        return backupDao.getEntityWithId(backupFileId, usernameProvider.getUsername()!!)!!.filePath
    }

    override suspend fun deleteCachedBackupFiles() {
        backupDao.getAllBackupFiles(usernameProvider.getUsername()!!)?.forEach {
            try {
                fileWrapper.delete(it)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
}