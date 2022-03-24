package ir.mehdiyari.krypt.ui.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.crypto.FileCrypt
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.di.qualifiers.AccountName
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.utils.ThumbsUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val fileCrypt: FileCrypt,
    private val filePathGenerator: FilePathGenerator,
    private val filesRepository: FilesRepository,
    @AccountName private val currentAccountName: String?,
    private val mediaStoreManager: MediaStoreManager,
    private val thumbsUtils: ThumbsUtils
) : ViewModel() {

    private val _photosViewState = MutableStateFlow<PhotosViewState>(
        PhotosViewState.Default
    )
    val photosViewState: StateFlow<PhotosViewState> = _photosViewState

    private val _latestAction = MutableStateFlow(PhotosFragmentAction.DEFAULT)
    val viewAction: StateFlow<PhotosFragmentAction> = _latestAction

    fun onActionReceived(
        action: PhotosFragmentAction
    ) {
        viewModelScope.launch {
            _latestAction.emit(action)
        }
    }

    fun onSelectedPhotos(photos: Array<String>) {
        viewModelScope.launch {
            _photosViewState.emit(
                PhotosViewState.EncryptDecryptState(
                    photos.size
                ) { delete ->
                    val action = viewAction.value
                    if (action == PhotosFragmentAction.PICK_PHOTO ||
                        action == PhotosFragmentAction.TAKE_PHOTO
                    ) {
                        encrypt(photos, delete)
                    } else if (action == PhotosFragmentAction.DECRYPT_PHOTO) {
                        decrypt(photos, delete)
                    }
                }
            )
        }
    }

    private fun encrypt(
        photos: Array<String>,
        deleteAfterEncrypt: Boolean
    ) {
        viewModelScope.launch(ioDispatcher) {
            _photosViewState.emit(PhotosViewState.OperationStart)
            val encryptedResults = mutableListOf<Pair<String, String?>>()
            photos.forEach { photoPath ->
                val destinationPath = filePathGenerator.generateFilePathForPhotos(photoPath)
                var thumbnailPath: String? = createThumbnailPath(destinationPath)
                try {
                    thumbsUtils.createThumbnailFromPath(photoPath, thumbnailPath!!)
                } catch (t: Throwable) {
                    thumbnailPath = null
                }

                if (fileCrypt.encryptFileToPath(photoPath, destinationPath)) {
                    encryptedResults.add(destinationPath to encryptThumbnail(thumbnailPath))
                } else {
                    return@forEach
                }

                if (thumbnailPath != null) {
                    try {
                        File(thumbnailPath).delete()
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }

            if (photos.size == encryptedResults.size) {
                if (deleteAfterEncrypt) {
                    try {
                        mediaStoreManager.deleteFilesFromExternalStorageAndMediaStore(photos.toList())
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }

                filesRepository.insertFiles(encryptedResults.map {
                    FileEntity(
                        type = FileTypeEnum.Photo,
                        filePath = it.first,
                        metaData = it.second ?: "",
                        accountName = currentAccountName!!
                    )
                })

                _photosViewState.emit(PhotosViewState.OperationFinished)
            } else {
                _photosViewState.emit(PhotosViewState.OperationFailed)
            }
        }
    }

    private fun encryptThumbnail(thumbnailPath: String?): String? = if (thumbnailPath != null) {
        try {
            val thumbEncryptedPath =
                filePathGenerator.generateFilePathForPhotosThumbnail(thumbnailPath)
            if (fileCrypt.encryptFileToPath(thumbnailPath, thumbEncryptedPath)) {
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

    private fun createThumbnailPath(destinationPath: String): String =
        "${filePathGenerator.getCashDir()}/thumb_${
            filePathGenerator.getNameOfFile(destinationPath)
        }.jpg"

    private fun decrypt(
        photos: Array<String>,
        deleteAfterEncrypt: Boolean
    ) {
        TODO()
    }
}