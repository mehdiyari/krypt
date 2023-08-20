package ir.mehdiyari.krypt.mediaList.data

import ir.mehdiyari.fallery.models.BucketType
import ir.mehdiyari.fallery.models.Media
import ir.mehdiyari.fallery.repo.AbstractBucketContentProvider
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.file.data.entity.FileEntity
import ir.mehdiyari.krypt.file.data.entity.FileTypeEnum
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import ir.mehdiyari.krypt.mediaList.utils.ThumbsUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

internal class EncryptedMediasBucketContentProvider @Inject constructor(
    private val filesRepository: FilesRepository,
    private val kryptCryptographyHelper: KryptCryptographyHelper,
    private val filesUtilities: FilesUtilities,
    private val thumbsUtils: ThumbsUtils
) : AbstractBucketContentProvider {

    override suspend fun getMediasOfBucket(
        bucketId: Long,
        bucketType: BucketType
    ): Flow<List<Media>> {
        return try {
            flow {
                when (bucketId) {
                    EncryptedMediasBucketProvider.KRYPT_SAFE_FOLDER_ID -> filesRepository.getAllEncryptedMedia()
                    EncryptedMediasBucketProvider.KRYPT_SAFE_FOLDER_PHOTO_ID -> filesRepository.getAllImages()
                    EncryptedMediasBucketProvider.KRYPT_SAFE_FOLDER_VIDEO_ID -> filesRepository.getAllVideos()
                    else -> TODO()
                }.map {
                    if (it.metaData.isNotBlank()) {
                        val finalPath =
                            filesUtilities.generateStableNameFilePathForMediaThumbnail(it.metaData)
                        if (!File(finalPath).exists()) {
                            createThumbnailBasedOnEncryptedMetaData(it, finalPath)
                        } else {
                            it to finalPath
                        }
                    } else {
                        createThumbnailForMedia(it)
                    }
                }.map {
                    val dimension =
                        if (!it.second.contains("/")) null else {
                            thumbsUtils.getPhotoDimension(it.second)
                        }

                    if (it.first.type == FileTypeEnum.Video) {
                        Media.Video(
                            id = it.first.id,
                            path = it.first.filePath,
                            duration = 0, // TODO: Read this from video metadata
                            thumbnail = Media.Photo(
                                id = it.first.id,
                                path = it.second,
                                width = dimension?.first ?: 0,
                                height = dimension?.second ?: 0
                            )
                        )
                    } else {
                        Media.Photo(
                            id = it.first.id,
                            path = it.second,
                            width = dimension?.first ?: 0,
                            height = dimension?.second ?: 0
                        )
                    }
                }.also {
                    emit(it)
                }
            }
        } catch (t: Throwable) {
            flow {
                this.emit(listOf())
            }
        } finally {
            filesUtilities.deleteCachedVideoDIR()
        }
    }

    // TODO: We should refactor this login in future
    private suspend fun createThumbnailForMedia(it: FileEntity): Pair<FileEntity, String> {
        return try {
            val isPhoto = it.type == FileTypeEnum.Photo
            val fileRealPath = filesUtilities.generateFilePathForMedia(
                mediaPath = it.filePath,
                isPhoto = isPhoto
            )

            if (kryptCryptographyHelper.decryptFile(it.filePath, fileRealPath).isSuccess) {
                var thumbnailPath: String? =
                    filesUtilities.createThumbnailPath(fileRealPath)

                try {
                    if (isPhoto) {
                        thumbsUtils.createThumbnailFromPath(
                            fileRealPath,
                            thumbnailPath!!
                        )
                    } else {
                        thumbsUtils.createVideoThumbnail(
                            fileRealPath,
                            thumbnailPath!!
                        )
                    }
                } catch (t: Throwable) {
                    thumbnailPath = null
                }

                File(fileRealPath).delete()
                val encryptedThumb = encryptThumbnail(thumbnailPath)
                if (encryptedThumb != null) {
                    val finalPath =
                        filesUtilities.generateStableNameFilePathForMediaThumbnail(encryptedThumb)
                    val newFileEntity = it.copy(metaData = encryptedThumb)
                    filesRepository.updateFile(newFileEntity)
                    createThumbnailBasedOnEncryptedMetaData(
                        newFileEntity, finalPath
                    )
                } else {
                    it to filesUtilities.getNameOfFile(it.filePath)
                }
            } else {
                it to filesUtilities.getNameOfFile(it.filePath)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            it to filesUtilities.getNameOfFile(it.filePath)
        }
    }

    private suspend fun createThumbnailBasedOnEncryptedMetaData(
        fileEntity: FileEntity,
        finalPath: String
    ) = if (kryptCryptographyHelper.decryptFile(
            fileEntity.metaData,
            finalPath
        ).isSuccess
    ) {
        fileEntity to finalPath
    } else {
        fileEntity to filesUtilities.getNameOfFile(fileEntity.filePath)
    }

    private suspend fun encryptThumbnail(thumbnailPath: String?): String? =
        if (thumbnailPath != null) {
            try {
                val thumbEncryptedPath =
                    filesUtilities.generateEncryptedFilePathForMediaThumbnail(thumbnailPath)
                if (kryptCryptographyHelper.encryptFile(
                        thumbnailPath,
                        thumbEncryptedPath
                    ).isSuccess
                ) {
                    thumbEncryptedPath
                } else {
                    null
                }
            } catch (t: Throwable) {
                null
            }
        } else {
            null
        }

}