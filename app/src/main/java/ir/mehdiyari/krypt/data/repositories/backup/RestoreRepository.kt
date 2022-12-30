package ir.mehdiyari.krypt.data.repositories.backup

import ir.mehdiyari.krypt.crypto.*
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.utils.FilesUtilities
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

class RestoreRepository @Inject constructor(
    private val fileUtils: FilesUtilities,
    private val fileCrypt: dagger.Lazy<FileCrypt>,
    private val dbBackupModelJsonAdapter: DBBackupModelJsonAdapter,
) {

    fun restoreAll(backupFile: String): Boolean {
        val newBackupPath = fileUtils.generateRestoreFilePath()
        if (!fileCrypt.get().decryptFileToPath(backupFile, newBackupPath)) {
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

            val dbBackupModel = getDataBaseModel(
                String(
                    fileCrypt.get().decryptDataWithIVAtFirst(dataBaseContent)!!
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