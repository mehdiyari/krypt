package ir.mehdiyari.krypt.backup.logic.restore

import ir.mehdiyari.krypt.accounts.data.dao.AccountsDao
import ir.mehdiyari.krypt.backup.logic.backup.DBBackupModel
import ir.mehdiyari.krypt.backup.logic.backup.DBBackupModelJsonAdapter
import ir.mehdiyari.krypt.cryptography.api.ByteCryptography
import ir.mehdiyari.krypt.cryptography.api.FileCryptography
import ir.mehdiyari.krypt.cryptography.exceptions.DecryptException
import ir.mehdiyari.krypt.cryptography.utils.HashingUtils
import ir.mehdiyari.krypt.cryptography.utils.SymmetricHelper
import ir.mehdiyari.krypt.cryptography.utils.getAfterIndex
import ir.mehdiyari.krypt.cryptography.utils.getBeforeIndex
import ir.mehdiyari.krypt.cryptography.utils.getBestBufferSizeForFile
import ir.mehdiyari.krypt.cryptography.utils.toLong
import ir.mehdiyari.krypt.file.data.dao.FilesDao
import ir.mehdiyari.krypt.file.data.entity.FileTypeEnum
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import javax.crypto.SecretKey
import javax.inject.Inject

internal class RestoreRepositoryImpl @Inject constructor(
    private val fileUtils: FilesUtilities,
    private val fileCryptography: FileCryptography,
    private val bytesCryptography: ByteCryptography,
    private val dbBackupModelJsonAdapter: DBBackupModelJsonAdapter,
    private val accountsDao: AccountsDao,
    private val filesDao: FilesDao,
) : RestoreRepository {

    override suspend fun restoreAll(backupFile: String, key: SecretKey): Result<Unit> {
        val newBackupPath = fileUtils.generateRestoreFilePath()
        val backupStream = FileInputStream(File(backupFile))
        val salt = ByteArray(HashingUtils.SALT_SIZE)
        backupStream.read(salt)

        if (fileCryptography.decryptFile(backupStream, newBackupPath, key).isFailure) {
            fileUtils.deleteFiles(newBackupPath)
            return Result.failure(DecryptException("Error in Decrypt backup file with given key."))
        }

        val file = File(newBackupPath)
        if (!file.exists()) {
            return Result.failure(FileNotFoundException("Decrypt finished but the output could not found. $newBackupPath"))
        }

        val fileList = mutableListOf<Pair<Long, String>>()
        try {
            FileInputStream(file).use { inputStream ->
                val dataBaseSize = ByteArray(Long.SIZE_BYTES)
                inputStream.read(dataBaseSize)

                val dataBaseContent = ByteArray(dataBaseSize.toLong().toInt())
                inputStream.read(dataBaseContent)

                val decryptInitialVector =
                    dataBaseContent.getBeforeIndex(SymmetricHelper.INITIALIZE_VECTOR_SIZE)
                val dbBackupModel = getDataBaseModel(
                    String(
                        bytesCryptography.decryptBytes(
                            dataBaseContent.getAfterIndex(SymmetricHelper.INITIALIZE_VECTOR_SIZE),
                            decryptInitialVector,
                            key,
                        ).getOrThrow()
                    )
                )

                repeat(dbBackupModel.files.size) {
                    val fileSize = ByteArray(Long.SIZE_BYTES)
                    val id = ByteArray(Long.SIZE_BYTES)

                    inputStream.read(fileSize)
                    val fileSizeInLong = fileSize.toLong()
                    inputStream.read(id)
                    val fileId = id.toLong()


                    val path: String = dbBackupModel.files.firstOrNull { file ->
                        file.id == fileId
                    }.let { currentFile ->
                        when (currentFile?.type!!) {
                            FileTypeEnum.Photo -> fileUtils.generateFilePathForMedia(
                                currentFile.filePath,
                                true
                            )

                            FileTypeEnum.Video -> fileUtils.generateFilePathForMedia(
                                currentFile.filePath,
                                false
                            )

                            FileTypeEnum.Text -> fileUtils.generateTextFilePath()
                            FileTypeEnum.Audio -> fileUtils.getRealFilePathForRecordedVoice()
                        }
                    }

                    FileOutputStream(File(path)).use { currentOutputStream ->
                        val bufferSize = getBestBufferSizeForFile(fileSizeInLong)
                        var readCount = 0L
                        while (true) {
                            if (readCount == fileSizeInLong) {
                                break
                            } else {
                                val buffer = ByteArray(bufferSize)
                                readCount += inputStream.read(buffer)
                                if (readCount > fileSizeInLong) {
                                    val fullSize = (readCount - fileSizeInLong).toInt()
                                    currentOutputStream.write(buffer.getBeforeIndex(fullSize))
                                } else {
                                    currentOutputStream.write(buffer)
                                }
                            }
                        }

                        fileList.add(fileId to path)
                    }
                }

                createDB(dbBackupModel.copy(files = dbBackupModel.files.map { fileEntity ->
                    fileList.firstOrNull { it.first == fileEntity.id }?.let {
                        fileEntity.copy(filePath = it.second)
                    } ?: fileEntity.copy(filePath = "")
                }))
            }
        } catch (t: Throwable) {
            fileUtils.deleteFiles(*fileList.map { it.second }.toTypedArray())
            return Result.failure(t)
        } finally {
            fileUtils.deleteFiles(newBackupPath)
        }

        return Result.success(Unit)
    }

    private suspend fun createDB(
        dbBackupModel: DBBackupModel,
    ) {
        accountsDao.insert(dbBackupModel.account)
        filesDao.insertFiles(dbBackupModel.files.map {
            it.copy(
                id = 0L,
                accountName = dbBackupModel.account.name,
                metaData = getMetaDataBasedOnType(it.metaData, it.type)
            )
        })
    }

    private fun getMetaDataBasedOnType(metaData: String, type: FileTypeEnum?): String {
        return if (type == FileTypeEnum.Photo || type == FileTypeEnum.Video) {
            ""
        } else {
            metaData
        }
    }

    private fun getDataBaseModel(dpContent: String): DBBackupModel = dbBackupModelJsonAdapter
        .fromJson(dpContent)!!
}