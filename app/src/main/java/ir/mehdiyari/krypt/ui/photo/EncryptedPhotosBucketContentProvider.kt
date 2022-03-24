package ir.mehdiyari.krypt.ui.photo

import ir.mehdiyari.fallery.models.BucketType
import ir.mehdiyari.fallery.models.Media
import ir.mehdiyari.fallery.repo.AbstractBucketContentProvider
import ir.mehdiyari.krypt.crypto.FileCrypt
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.di.qualifiers.AccountName
import ir.mehdiyari.krypt.utils.FilesUtilities
import ir.mehdiyari.krypt.utils.ThumbsUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class EncryptedPhotosBucketContentProvider @Inject constructor(
    @AccountName private val accountName: String,
    private val filesRepository: FilesRepository,
    private val fileCrypt: FileCrypt,
    private val filesUtilities: FilesUtilities,
    private val thumbsUtils: ThumbsUtils
) : AbstractBucketContentProvider {

    override suspend fun getMediasOfBucket(
        bucketId: Long,
        bucketType: BucketType
    ): Flow<List<Media>> {
        return if (bucketId == EncryptedPhotosBucketProvider.KRYPT_SAFE_FOLDER_ID) {
            flow {
                filesRepository.getAllPhotosForCurrentUser(accountName).map {
                    if (it.metaData.isNotBlank()) {
                        val finalPath =
                            filesUtilities.generateStableNameFilePathForPhotosThumbnail(it.metaData)

                        if (!File(finalPath).exists()) {
                            if (fileCrypt.decryptFileToPath(it.metaData, finalPath)) {
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

                    Media.Photo(
                        id = it.first.id,
                        path = it.second,
                        width = dimension?.first ?: 0,
                        height = dimension?.second ?: 0
                    )
                }.also {
                    emit(it)
                }
            }
        } else {
            flow {
                this.emit(listOf())
            }
        }
    }

}