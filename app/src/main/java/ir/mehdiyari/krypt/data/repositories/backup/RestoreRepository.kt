package ir.mehdiyari.krypt.data.repositories.backup

import ir.mehdiyari.krypt.crypto.api.ByteCryptography
import ir.mehdiyari.krypt.crypto.api.FileCryptography
import ir.mehdiyari.krypt.crypto.utils.SymmetricHelper
import ir.mehdiyari.krypt.crypto.utils.getAfterIndex
import ir.mehdiyari.krypt.crypto.utils.getBeforeIndex
import ir.mehdiyari.krypt.crypto.utils.getBestBufferSizeForFile
import ir.mehdiyari.krypt.crypto.utils.toLong
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.utils.FilesUtilities
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.SecretKey


class RestoreRepository(
    private val fileUtils: FilesUtilities,
    private val fileCryptography: FileCryptography,
    private val bytesCryptography: ByteCryptography,
    private val dbBackupModelJsonAdapter: DBBackupModelJsonAdapter,
    private val restoreKey: SecretKey,
) {

    suspend fun restoreAll(backupFile: String): Boolean {
        val newBackupPath = fileUtils.generateRestoreFilePath()
        if (fileCryptography.decryptFile(backupFile, newBackupPath, restoreKey).isFailure) {
            return false
        }

        val file = File(newBackupPath)
        if (!file.exists()) {
            return false
        }

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
                        restoreKey,
                    ).getOrThrow()
                )
            )

            val fileList = mutableListOf<Pair<Long, String>>()
            var nextFileReadedByteArray: ByteArray? = null

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
                                nextFileReadedByteArray = buffer.getAfterIndex(
                                    fullSize
                                )
                            } else {
                                currentOutputStream.write(buffer)
                            }
                        }
                    }

                    fileList.add(fileId to path)
                }
            }
        }

        return true
    }

    private fun getDataBaseModel(dpContent: String): DBBackupModel = dbBackupModelJsonAdapter
        .fromJson(dpContent)!!
}