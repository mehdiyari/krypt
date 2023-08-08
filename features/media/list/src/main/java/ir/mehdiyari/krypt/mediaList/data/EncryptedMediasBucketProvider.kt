package ir.mehdiyari.krypt.mediaList.data

import android.app.Application
import ir.mehdiyari.fallery.models.BucketType
import ir.mehdiyari.fallery.models.MediaBucket
import ir.mehdiyari.fallery.repo.AbstractMediaBucketProvider
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import ir.mehdiyari.krypt.mediaList.R
import java.io.File
import javax.inject.Inject

internal class EncryptedMediasBucketProvider @Inject constructor(
    private val filesRepository: FilesRepository,
    private val filesUtilities: FilesUtilities,
    private val kryptCryptographyHelper: KryptCryptographyHelper,
    private val context: Application
) : AbstractMediaBucketProvider {

    companion object {
        const val KRYPT_SAFE_FOLDER_ID = 1L
        const val KRYPT_SAFE_FOLDER_PHOTO_ID = 2L
        const val KRYPT_SAFE_FOLDER_VIDEO_ID = 3L
    }

    override suspend fun getMediaBuckets(bucketType: BucketType): List<MediaBucket> {
        val list = mutableListOf<MediaBucket>()
        if (filesRepository.getMediasCount() != 0L) {
            list.add(
                MediaBucket(
                    id = KRYPT_SAFE_FOLDER_ID,
                    bucketPath = filesUtilities.getFilesDir(),
                    displayName = context.getString(R.string.all_media),
                    firstMediaThumbPath = getFirstThumbnail(filesRepository.getLastEncryptedMediaThumbnail())
                        ?: "",
                    mediaCount = filesRepository.getMediasCount().toInt()
                )
            )
        }

        if (filesRepository.getPhotosCount() != 0L) {
            list.add(
                MediaBucket(
                    id = KRYPT_SAFE_FOLDER_PHOTO_ID,
                    bucketPath = filesUtilities.getFilesDir(),
                    displayName = context.getString(R.string.photos_folder_name),
                    firstMediaThumbPath = getFirstThumbnail(filesRepository.getLastEncryptedPhotoThumbnail())
                        ?: "",
                    mediaCount = filesRepository.getPhotosCount().toInt()
                )
            )
        }

        if (filesRepository.getVideosCount() != 0L) {
            list.add(
                MediaBucket(
                    id = KRYPT_SAFE_FOLDER_VIDEO_ID,
                    bucketPath = filesUtilities.getFilesDir(),
                    displayName = context.getString(R.string.videos_folder_name),
                    firstMediaThumbPath = getFirstThumbnail(filesRepository.getLastEncryptedVideoThumbnail())
                        ?: "",
                    mediaCount = filesRepository.getVideosCount().toInt()
                )
            )
        }

        return list
    }

    private suspend fun getFirstThumbnail(thumb: String?): String? =
        thumb?.let {
            val finalPath = filesUtilities.generateStableNameFilePathForMediaThumbnail(it)
            if (!File(finalPath).exists()) {
                if (kryptCryptographyHelper.decryptFile(it, finalPath).isSuccess) {
                    finalPath
                } else {
                    null
                }
            } else {
                finalPath
            }
        }
}