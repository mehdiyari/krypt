package ir.mehdiyari.krypt.data.repositories.backup

import ir.mehdiyari.krypt.crypto.KryptCryptographyHelper
import ir.mehdiyari.krypt.crypto.SymmetricHelper
import ir.mehdiyari.krypt.crypto.combineWith
import ir.mehdiyari.krypt.crypto.getBestBufferSizeForFile
import ir.mehdiyari.krypt.crypto.toByteArray
import ir.mehdiyari.krypt.crypto.toUtf8Bytes
import ir.mehdiyari.krypt.data.account.AccountEntity
import ir.mehdiyari.krypt.data.account.AccountsDao
import ir.mehdiyari.krypt.data.backup.BackupDao
import ir.mehdiyari.krypt.data.backup.BackupEntity
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FilesDao
import ir.mehdiyari.krypt.data.repositories.CurrentUser
import ir.mehdiyari.krypt.utils.FilesUtilities
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val accountsDao: AccountsDao,
    private val filesDao: FilesDao,
    private val backupDao: BackupDao,
    private val currentUser: CurrentUser,
    private val dbBackupModelJsonAdapter: DBBackupModelJsonAdapter,
    private val fileUtils: FilesUtilities,
    private val kryptCryptographyHelper: KryptCryptographyHelper,
    private val symmetricHelper: SymmetricHelper,
    private val key: dagger.Lazy<SecretKey>,
) {

    /**
     *  Backup from all data's in db with all encrypted files.
     *
     *  Output File Schema:
     *  [^IV[EN_PART:[^DB_SIZE, ^EN_DB, ^F_SIZE, ^EN_F_CONTENT, ^F_SIZE, ^EN_F_CONTENT]]]
     */
    suspend fun backupAll(): Boolean {
        val user = accountsDao.getAccountWithName(currentUser.accountName!!)!!
        val files = filesDao.getAllFiles(currentUser.accountName!!).filter {
            File(it.filePath).exists()
        }

        val backupFilePath = fileUtils.generateBackupFilePath(currentUser.accountName!!)
        val backupFile = File(backupFilePath)

        val (cipher, initVector) = symmetricHelper.getAESCipher() to symmetricHelper.createInitVector()
        cipher.init(Cipher.ENCRYPT_MODE, key.get(), IvParameterSpec(initVector))

        FileOutputStream(backupFile).use { backupStream ->

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
                account = currentUser.accountName!!
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

    suspend fun getBackupRecord(): List<BackupEntity> = backupDao.getAllBackups(
        currentUser.accountName!!
    )

    suspend fun getLastBackUpDateTime(): String {
        val dateTime = backupDao.getLastBackupRecord(currentUser.accountName!!)
        return if (dateTime == null) {
            ""
        } else {
            convertToBackUpDateTimeFormat(dateTime)
        }
    }

    fun convertToBackUpDateTimeFormat(dateTime: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        return try {
            simpleDateFormat.format(dateTime)
        } catch (t: Throwable) {
            ""
        }
    }

    suspend fun deleteBackupWithId(backupFileId: Int) {
        backupDao.getEntityWithId(
            backupFileId, currentUser.accountName!!
        )!!.also {
            File(it.filePath).delete()
            backupDao.deleteBackupWithId(backupFileId, currentUser.accountName!!)
        }
    }

    suspend fun getBackupFilePathWithId(backupFileId: Int): String {
        return backupDao.getEntityWithId(backupFileId, currentUser.accountName!!)!!.let {
            it.filePath
        }
    }

    suspend fun deleteCachedBackupFiles() {
        backupDao.getAllBackupFiles(currentUser.accountName!!)?.forEach {
            try {
                File(it).delete()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
}