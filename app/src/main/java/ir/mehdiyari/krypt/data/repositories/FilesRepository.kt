package ir.mehdiyari.krypt.data.repositories

import ir.mehdiyari.krypt.data.backup.BackupDao
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.file.FilesDao
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.utils.FilesUtilities
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilesRepository @Inject constructor(
    private val filedDao: FilesDao,
    private val backupDao: BackupDao,
    private val currentUser: CurrentUser,
    private val filesUtilities: FilesUtilities,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
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

    suspend fun getLastEncryptedMediaThumbnail(): String? =
        internalGetLastThumb(FileTypeEnum.Photo, FileTypeEnum.Video)

    suspend fun getLastEncryptedPhotoThumbnail(): String? =
        internalGetLastThumb(FileTypeEnum.Photo)

    suspend fun getLastEncryptedVideoThumbnail(): String? =
        internalGetLastThumb(FileTypeEnum.Video)

    private suspend fun internalGetLastThumb(
        vararg types: FileTypeEnum
    ): String? = filedDao.getAllFilesOfCurrentAccountBasedOnType(
        currentUser.accountName!!,
        *types
    ).lastOrNull {
        it.metaData.isNotBlank()
    }?.metaData

    suspend fun getAllEncryptedMedia(): List<FileEntity> =
        filedDao.getAllMedia(
            currentUser.accountName!!
        )

    suspend fun mapThumbnailsAndNameToFileEntity(medias: Array<String>): List<FileEntity> =
        mutableListOf<FileEntity>().apply {
            getAllEncryptedMedia().filter {
                medias.any { currentMedia ->
                    if (!currentMedia.contains("/")) {
                        it.filePath.contains(currentMedia)
                    } else {
                        val nameOfFile = filesUtilities.getNameOfFileWithExtension(currentMedia)
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

    suspend fun getAllImages(): List<FileEntity> = filedDao.getAllMedia(
        currentUser.accountName!!, listOf(FileTypeEnum.Photo)
    )

    suspend fun getAllVideos(): List<FileEntity> = filedDao.getAllMedia(
        currentUser.accountName!!, listOf(FileTypeEnum.Video)
    )

    suspend fun getPhotosCount(): Long = filedDao.getFilesCountBasedOnType(
        currentUser.accountName!!,
        FileTypeEnum.Photo
    )

    suspend fun getAudiosCount(): Long = withContext(ioDispatcher) {
        filedDao.getFilesCountBasedOnType(
            currentUser.accountName!!,
            FileTypeEnum.Audio
        )
    }

    suspend fun getVideosCount(): Long = filedDao.getFilesCountBasedOnType(
        currentUser.accountName!!,
        FileTypeEnum.Video
    )

    suspend fun getFileByThumbPath(thumbFileName: String): FileEntity? =
        filedDao.getMediaFileByPath(
            currentUser.accountName!!, thumbFileName
        )

    suspend fun getAllAudioFiles(): List<FileEntity> = withContext(ioDispatcher) {
        filedDao.getAllFilesOfCurrentAccountBasedOnType(
            currentUser.accountName!!,
            FileTypeEnum.Audio
        )
    }

    suspend fun updateFile(fileEntity: FileEntity): Unit = withContext(ioDispatcher) {
        filedDao.updateFile(fileEntity)
    }
}