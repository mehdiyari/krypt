package ir.mehdiyari.krypt.data.repositories

import ir.mehdiyari.krypt.data.backup.BackupDao
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.file.FilesDao
import ir.mehdiyari.krypt.utils.FilesUtilities
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesRepository @Inject constructor(
    private val filedDao: FilesDao,
    private val backupDao: BackupDao,
    private val currentUser: CurrentUser,
    private val filesUtilities: FilesUtilities
) {

    suspend fun getAllFilesTypeCounts(): List<Pair<FileTypeEnum, Long>> =
        mutableListOf<Pair<FileTypeEnum, Long>>().apply {
            FileTypeEnum.values().forEach { fileType ->
                add(
                    fileType to try {
                        filedDao.getFilesCountBasedOnType(
                            currentUser.accountName!!,
                            fileType
                        )
                    } catch (t: Throwable) {
                        0
                    }
                )
            }
        }.toList()


    suspend fun insertFiles(
        files: List<FileEntity>
    ) {
        filedDao.insertFiles(files.map {
            it.copy(accountName = currentUser.accountName!!)
        })
    }

    suspend fun getMediasCount(): Long = filedDao.getFilesCountBasedOnType(
        currentUser.accountName!!,
        FileTypeEnum.Photo
    ) + filedDao.getFilesCountBasedOnType(
        currentUser.accountName!!,
        FileTypeEnum.Video
    )

    suspend fun getLastEncryptedPhotoThumbnail(): String? =
        filedDao.getAllFilesOfCurrentAccountBasedOnType(
            currentUser.accountName!!,
            FileTypeEnum.Photo, FileTypeEnum.Video
        ).lastOrNull {
            it.metaData.isNotBlank()
        }?.metaData

    suspend fun getAllEncryptedMedia(): List<FileEntity> =
        filedDao.getAllMedia(
            currentUser.accountName!!
        )

    suspend fun mapThumbnailsAndNameToFileEntity(photos: Array<String>): List<FileEntity> =
        mutableListOf<FileEntity>().apply {
            getAllEncryptedMedia().filter {
                photos.any { currentPhoto ->
                    if (!currentPhoto.contains("/")) {
                        it.filePath.contains(currentPhoto)
                    } else {
                        val nameOfFile = filesUtilities.getNameOfFile(currentPhoto)
                        it.metaData.contains(nameOfFile) || it.filePath.contains(nameOfFile)
                    }
                }
            }.also(this::addAll)
        }

    suspend fun deleteEncryptedFilesFromKryptDBAndFileSystem(files: List<FileEntity>) {
        filedDao.deleteFiles(files)
        files.forEach {
            File(it.filePath).delete()
            if (
                it.metaData.isNotBlank()
                && (it.type == FileTypeEnum.Photo || it.type == FileTypeEnum.Video)
            ) {
                File(it.metaData).delete()
            }
        }
    }

    suspend fun getAllTextFiles(): List<FileEntity> =
        filedDao.getAllFilesOfCurrentAccountBasedOnType(
            currentUser.accountName!!,
            FileTypeEnum.Text
        )

    suspend fun getFileById(id: Long): FileEntity? =
        filedDao.getFileById(currentUser.accountName!!, id)

    suspend fun getAllFiles(): List<FileEntity> = filedDao.getAllFiles(currentUser.accountName!!)

    suspend fun getAllFilesSize(): Long {
        var total = 0L
        (try {
            mutableListOf<String>().apply {
                addAll(backupDao.getAllBackupFiles(currentUser.accountName!!) ?: listOf())
                addAll(filedDao.getAllFilesPath(currentUser.accountName!!) ?: listOf())
            }
        } catch (t: Throwable) {
            null
        })?.map {
            File(it).length()
        }?.forEach {
            total += it
        }

        return total
    }
}