package ir.mehdiyari.krypt.ui.media

import ir.mehdiyari.fallery.models.BucketType
import ir.mehdiyari.fallery.models.Media
import ir.mehdiyari.fallery.repo.AbstractBucketContentProvider
import ir.mehdiyari.krypt.crypto.KryptCryptographyHelper
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.utils.FilesUtilities
import ir.mehdiyari.krypt.utils.ThumbsUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class EncryptedMediasBucketContentProvider @Inject constructor(
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
                            if (kryptCryptographyHelper.decryptFile(
                                    it.metaData,
                                    finalPath
                                ).isSuccess
                            ) {
                                it to finalPath
                            } else {
                                it to filesUtilities.getNameOfFile(it.filePath)
                            }
                        } else {
                            it to finalPath
                        }
                    } else {
                        it to filesUtilities.getNameOfFile(it.filePath)
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
                            duration = 0, // todo: must be read from metadata
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
        }
    }

}