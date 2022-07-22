package ir.mehdiyari.krypt.data.repositories

import ir.mehdiyari.krypt.data.backup.BackupDao
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.file.FilesDao
import ir.mehdiyari.krypt.di.qualifiers.AccountName
import ir.mehdiyari.krypt.utils.FilesUtilities
import java.io.File
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class FilesRepository @Inject constructor(
    private val filedDao: FilesDao,
    private val backupDao: BackupDao,
    @AccountName private val currentAccountName: Provider<String>,
    private val filesUtilities: FilesUtilities
) {

    suspend fun getAllFilesTypeCounts(): List<Pair<FileTypeEnum, Long>> =
        mutableListOf<Pair<FileTypeEnum, Long>>().apply {
            FileTypeEnum.values().forEach { fileType ->
                add(
                    fileType to try {
                        filedDao.getFilesCountBasedOnType(
                            currentAccountName.get(),
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
        filedDao.insertFiles(files)
    }

    suspend fun getMediasCount(): Long = filedDao.getFilesCountBasedOnType(
        currentAccountName.get(),
        FileTypeEnum.Photo
    ) + filedDao.getFilesCountBasedOnType(
        currentAccountName.get(),
        FileTypeEnum.Video
    )

    suspend fun getLastEncryptedPhotoThumbnail(): String? =
        filedDao.getAllFilesOfCurrentAccountBasedOnType(
            currentAccountName.get(),
            FileTypeEnum.Photo, FileTypeEnum.Video
        ).lastOrNull {
            it.metaData.isNotBlank()
        }?.metaData

    suspend fun getAllEncryptedMedia(): List<FileEntity> =
        filedDao.getAllMedia(
            currentAccountName.get()
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
        filedDao.getAllFilesOfCurrentAccountBasedOnType(currentAccountName.get(), FileTypeEnum.Text)

    suspend fun getFileById(id: Long): FileEntity? =
        filedDao.getFileById(currentAccountName.get(), id)

    suspend fun getAllFiles(): List<FileEntity> = filedDao.getAllFiles(currentAccountName.get())

    suspend fun getAllFilesSize(): Long {
        var total = 0L
        (try {
            mutableListOf<String>().apply {
                addAll(backupDao.getAllBackupFiles(currentAccountName.get()) ?: listOf())
                addAll(filedDao.getAllFilesPath(currentAccountName.get()) ?: listOf())
            }
        } catch (t: Throwable) {
            null
        })?.map {
            File(it).length()
        }?.forEach {
            total += convectToMB(it)
        }

        return total
    }

    private fun convectToMB(size: Long): Long = (size / (1024)) / 1024
}