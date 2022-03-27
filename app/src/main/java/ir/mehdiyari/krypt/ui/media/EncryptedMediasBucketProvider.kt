package ir.mehdiyari.krypt.ui.media

import android.app.Application
import ir.mehdiyari.fallery.models.BucketType
import ir.mehdiyari.fallery.models.MediaBucket
import ir.mehdiyari.fallery.repo.AbstractMediaBucketProvider
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.crypto.FileCrypt
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.utils.FilesUtilities
import java.io.File
import javax.inject.Inject

class EncryptedMediasBucketProvider @Inject constructor(
    private val filesRepository: FilesRepository,
    private val filesUtilities: FilesUtilities,
    private val fileCrypt: FileCrypt,
    private val context: Application
) : AbstractMediaBucketProvider {

    companion object {
        const val KRYPT_SAFE_FOLDER_ID = 1L
    }

    override suspend fun getMediaBuckets(bucketType: BucketType): List<MediaBucket> {
        return listOf(
            MediaBucket(
                id = KRYPT_SAFE_FOLDER_ID,
                bucketPath = filesUtilities.getFilesDir(),
                displayName = context.getString(R.string.app_name),
                firstMediaThumbPath = getFirstThumbnail() ?: "",
                mediaCount = filesRepository.getMediasCount().toInt()
            )
        )
    }

    private suspend fun getFirstThumbnail(): String? =
        filesRepository.getLastEncryptedPhotoThumbnail()?.let {
            val finalPath = filesUtilities.generateStableNameFilePathForMediaThumbnail(it)
            if (!File(finalPath).exists()) {
                if (fileCrypt.decryptFileToPath(it, finalPath)) {
                    finalPath
                } else {
                    null
                }
            } else {
                finalPath
            }
        }
}