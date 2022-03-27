package ir.mehdiyari.krypt.ui.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.crypto.FileCrypt
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.di.qualifiers.AccountName
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.utils.FilesUtilities
import ir.mehdiyari.krypt.utils.MediaStoreManager
import ir.mehdiyari.krypt.utils.ThumbsUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MediasViewModel @Inject constructor(
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val fileCrypt: FileCrypt,
    private val filesUtilities: FilesUtilities,
    private val filesRepository: FilesRepository,
    @AccountName private val currentAccountName: String?,
    private val mediaStoreManager: MediaStoreManager,
    private val thumbsUtils: ThumbsUtils
) : ViewModel() {

    private val _mediaViewState = MutableStateFlow<MediaViewState>(
        MediaViewState.Default
    )
    val mediaViewState: StateFlow<MediaViewState> = _mediaViewState

    private val _latestAction = MutableStateFlow(MediaFragmentAction.DEFAULT)
    val viewAction: StateFlow<MediaFragmentAction> = _latestAction

    fun onActionReceived(
        action: MediaFragmentAction
    ) {
        viewModelScope.launch {
            _latestAction.emit(action)
        }
    }

    fun onSelectedMedias(medias: Array<String>) {
        viewModelScope.launch {
            _mediaViewState.emit(
                MediaViewState.EncryptDecryptState(
                    medias.size
                ) { delete ->
                    val action = viewAction.value
                    if (action == MediaFragmentAction.PICK_MEDIA ||
                        action == MediaFragmentAction.TAKE_MEDIA
                    ) {
                        encrypt(medias, delete)
                    } else if (action == MediaFragmentAction.DECRYPT_MEDIA) {
                        decrypt(medias, delete)
                    }
                }
            )
        }
    }

    private fun encrypt(
        medias: Array<String>,
        deleteAfterEncrypt: Boolean
    ) {
        viewModelScope.launch(ioDispatcher) {
            _mediaViewState.emit(MediaViewState.OperationStart)
            val encryptedResults = mutableListOf<Pair<String, String?>>()
            medias.forEach { mediaPath ->
                val destinationPath = filesUtilities.generateFilePathForPhotos(mediaPath)
                var thumbnailPath: String? = filesUtilities.createThumbnailPath(destinationPath)
                try {
                    thumbsUtils.createThumbnailFromPath(mediaPath, thumbnailPath!!)
                } catch (t: Throwable) {
                    thumbnailPath = null
                }

                if (fileCrypt.encryptFileToPath(mediaPath, destinationPath)) {
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

            if (medias.size == encryptedResults.size) {
                if (deleteAfterEncrypt) {
                    try {
                        mediaStoreManager.deleteFilesFromExternalStorageAndMediaStore(medias.toList())
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

                _mediaViewState.emit(MediaViewState.OperationFinished)
            } else {
                _mediaViewState.emit(MediaViewState.OperationFailed)
            }
        }
    }

    private fun encryptThumbnail(thumbnailPath: String?): String? = if (thumbnailPath != null) {
        try {
            val thumbEncryptedPath =
                filesUtilities.generateEncryptedFilePathForPhotosThumbnail(thumbnailPath)
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

    private fun decrypt(
        medias: Array<String>,
        deleteAfterEncrypt: Boolean
    ) {
        viewModelScope.launch(ioDispatcher) {
            _mediaViewState.emit(MediaViewState.OperationStart)
            val decryptedResult = mutableListOf<Pair<String, Long>>()
            val encryptedMedias = filesRepository.mapThumbnailsAndNameToFileEntity(medias)

            encryptedMedias.forEach { encryptedMedia ->
                val destinationPath = filesUtilities.generateDecryptedPhotoPathInKryptFolder(
                    encryptedMedia.filePath
                )

                if (fileCrypt.decryptFileToPath(encryptedMedia.filePath, destinationPath)) {
                    decryptedResult.add(destinationPath to encryptedMedia.id)
                }
            }

            if (medias.isNotEmpty() && decryptedResult.isEmpty()) {
                _mediaViewState.emit(MediaViewState.OperationFailed)
            } else {
                mediaStoreManager.scanAddedMedia(decryptedResult.map { it.first })
                if (deleteAfterEncrypt) {
                    val ids = decryptedResult.map { it.second }
                    filesRepository.deleteEncryptedFilesFromKryptDBAndFileSystem(encryptedMedias.filter {
                        ids.contains(it.id)
                    })
                }

                _mediaViewState.emit(MediaViewState.OperationFinished)
            }
        }
    }

    suspend fun checkForOpenPickerForDecryptMode(): Boolean = filesRepository.getMediasCount() > 0L
}