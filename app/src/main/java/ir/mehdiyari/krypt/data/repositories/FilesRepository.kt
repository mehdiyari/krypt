package ir.mehdiyari.krypt.data.repositories

import ir.mehdiyari.krypt.app.user.UsernameProvider
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
    private val usernameProvider: UsernameProvider,
    private val filesUtilities: FilesUtilities,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getAllFilesTypeCounts(): List<Pair<FileTypeEnum, Long>> =
        mutableListOf<Pair<FileTypeEnum, Long>>().apply {
            FileTypeEnum.values().forEach { fileType ->
                add(
                    fileType to try {
                        filedDao.getFilesCountBasedOnType(
                            usernameProvider.getUsername()!!,
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
            it.copy(accountName = usernameProvider.getUsername()!!)
        })
    }

    suspend fun getMediasCount(): Long = filedDao.getFilesCountBasedOnType(
        usernameProvider.getUsername()!!,
        FileTypeEnum.Photo
    ) + filedDao.getFilesCountBasedOnType(
        usernameProvider.getUsername()!!,
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
        usernameProvider.getUsername()!!,
        *types
    ).lastOrNull {
        it.metaData.isNotBlank()
    }?.metaData

    suspend fun getAllEncryptedMedia(): List<FileEntity> =
        filedDao.getAllMedia(
            usernameProvider.getUsername()!!
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
            usernameProvider.getUsername()!!,
            FileTypeEnum.Text
        )

    suspend fun getFileById(id: Long): FileEntity? =
        filedDao.getFileById(usernameProvider.getUsername()!!, id)

    suspend fun getAllFiles(): List<FileEntity> =
        filedDao.getAllFiles(usernameProvider.getUsername()!!)

    suspend fun getAllFilesSize(): Long {
        var total = 0L
        (try {
            mutableListOf<String>().apply {
                addAll(backupDao.getAllBackupFiles(usernameProvider.getUsername()!!) ?: listOf())
                addAll(filedDao.getAllFilesPath(usernameProvider.getUsername()!!) ?: listOf())
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
        usernameProvider.getUsername()!!, listOf(FileTypeEnum.Photo)
    )

    suspend fun getAllVideos(): List<FileEntity> = filedDao.getAllMedia(
        usernameProvider.getUsername()!!, listOf(FileTypeEnum.Video)
    )

    suspend fun getPhotosCount(): Long = filedDao.getFilesCountBasedOnType(
        usernameProvider.getUsername()!!,
        FileTypeEnum.Photo
    )

    suspend fun getAudiosCount(): Long = withContext(ioDispatcher) {
        filedDao.getFilesCountBasedOnType(
            usernameProvider.getUsername()!!,
            FileTypeEnum.Audio
        )
    }

    suspend fun getVideosCount(): Long = filedDao.getFilesCountBasedOnType(
        usernameProvider.getUsername()!!,
        FileTypeEnum.Video
    )

    suspend fun getFileByThumbPath(thumbFileName: String): FileEntity? =
        filedDao.getMediaFileByPath(
            usernameProvider.getUsername()!!, thumbFileName
        )

    suspend fun getAllAudioFiles(): List<FileEntity> = withContext(ioDispatcher) {
        filedDao.getAllFilesOfCurrentAccountBasedOnType(
            usernameProvider.getUsername()!!,
            FileTypeEnum.Audio
        )
    }

    suspend fun updateFile(fileEntity: FileEntity): Unit = withContext(ioDispatcher) {
        filedDao.updateFile(fileEntity)
    }

    suspend fun getAudioById(id: Long): FileEntity? = withContext(ioDispatcher) {
        filedDao.getFileById(usernameProvider.getUsername()!!, id)
    }
}