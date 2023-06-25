package ir.mehdiyari.krypt.ui.media

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.fallery.main.fallery.FalleryOptions
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.crypto.FileCrypt
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.ui.media.data.FalleryBuilderProvider
import ir.mehdiyari.krypt.utils.FilesUtilities
import ir.mehdiyari.krypt.utils.MediaStoreManager
import ir.mehdiyari.krypt.utils.ThumbsUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MediasViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val fileCrypt: FileCrypt,
    private val filesUtilities: FilesUtilities,
    private val filesRepository: FilesRepository,
    private val mediaStoreManager: MediaStoreManager,
    private val thumbsUtils: ThumbsUtils,
    private val falleryBuilderProvider: FalleryBuilderProvider,
) : ViewModel() {

    private val _mediaViewState = MutableStateFlow<MediaViewState>(
        MediaViewState.Default
    )
    val mediaViewState = _mediaViewState.asStateFlow()

    private val _latestAction = MutableStateFlow(MediaViewAction.DEFAULT)
    val viewAction = _latestAction.asStateFlow()

    private val _selectedMedias = MutableStateFlow<List<SelectedMediaItems>>(listOf())
    val selectedMediasFlow = _selectedMedias.asStateFlow()

    private val _messageFlow = MutableSharedFlow<Int>()
    val messageFlow = _messageFlow.asSharedFlow()

    private val args = MediaArgs(savedStateHandle)

    init {
        if (viewAction.value == MediaViewAction.DEFAULT) {
            viewModelScope.launch {
                _latestAction.emit(args.action)
            }
        }
    }

    fun onSelectedMedias(medias: List<String>) {
        viewModelScope.launch {
            if (isEncryptAction()) {
                _selectedMedias.emit(medias.map {
                    SelectedMediaItems(it, false)
                })
            } else if (isDecryptAction()) {
                _selectedMedias.emit(medias.map {
                    SelectedMediaItems(it, true)
                })
            }

            _mediaViewState.emit(
                MediaViewState.EncryptDecryptState(
                    selectedMediasFlow.value, getOnActionClickedCallback()
                )
            )
        }
    }

    private fun getOnActionClickedCallback(): (deleteAfterEncryption: Boolean, notifyMediaScanner: Boolean) -> Unit =
        { delete, notifyMediaScanner ->
            val items = selectedMediasFlow.value.map {
                it.path
            }.toTypedArray()

            if (isEncryptAction()) {
                encrypt(items, delete)
            } else if (isDecryptAction()) {
                decrypt(items, delete, notifyMediaScanner)
            }
        }

    private fun encrypt(
        medias: Array<String>,
        deleteAfterEncrypt: Boolean
    ) {
        viewModelScope.launch(ioDispatcher) {
            _mediaViewState.emit(MediaViewState.OperationStart)
            val encryptedResults = mutableListOf<Pair<Pair<Boolean, String>, String?>>()
            medias.forEach { mediaPath ->
                val isPhoto = filesUtilities.isPhotoPath(mediaPath)
                val destinationPath = filesUtilities.generateFilePathForMedia(mediaPath, isPhoto)
                var thumbnailPath: String? = filesUtilities.createThumbnailPath(destinationPath)
                try {
                    if (isPhoto) {
                        thumbsUtils.createThumbnailFromPath(mediaPath, thumbnailPath!!)
                    } else {
                        thumbsUtils.createVideoThumbnail(mediaPath, thumbnailPath!!)
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                    thumbnailPath = null
                }

                if (fileCrypt.encryptFileToPath(mediaPath, destinationPath)) {
                    encryptedResults.add(
                        (isPhoto to destinationPath) to encryptThumbnail(
                            thumbnailPath
                        )
                    )
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
                        type = if (it.first.first) FileTypeEnum.Photo else FileTypeEnum.Video,
                        filePath = it.first.second,
                        metaData = it.second ?: "",
                        accountName = ""
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
                filesUtilities.generateEncryptedFilePathForMediaThumbnail(thumbnailPath)
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
        deleteAfterEncrypt: Boolean,
        notifyMediaScanner: Boolean
    ) {
        viewModelScope.launch(ioDispatcher) {
            _mediaViewState.emit(MediaViewState.OperationStart)
            val decryptedResult = mutableListOf<Pair<String, Long>>()
            val encryptedMedias = filesRepository.mapThumbnailsAndNameToFileEntity(medias)

            encryptedMedias.forEach { encryptedMedia ->
                val destinationPath = if (encryptedMedia.type == FileTypeEnum.Photo) {
                    filesUtilities.generateDecryptedPhotoMediaInKryptFolder(
                        encryptedMedia.filePath
                    )
                } else {
                    filesUtilities.generateDecryptedVideoMediaInKryptFolder(
                        encryptedMedia.filePath
                    )
                }

                if (fileCrypt.decryptFileToPath(encryptedMedia.filePath, destinationPath)) {
                    decryptedResult.add(destinationPath to encryptedMedia.id)
                }
            }

            if (medias.isNotEmpty() && decryptedResult.isEmpty()) {
                _mediaViewState.emit(MediaViewState.OperationFailed)
            } else {
                if (notifyMediaScanner) {
                    mediaStoreManager.scanAddedMedia(decryptedResult.map { it.first })
                }
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

    fun checkForOpenPickerForDecryptMode(): Boolean = try {
        runBlocking { filesRepository.getMediasCount() > 0L }
    } catch (t: Throwable) {
        true
    }

    // TODO: disabled, we should refactor after migration
    fun onDecryptSharedMedia(images: List<Uri>?) {
        if (!images.isNullOrEmpty()) {
            onSelectedMedias(images.mapNotNull { filesUtilities.getPathFromUri(it) }.filter {
                filesUtilities.isPhotoPath(it) || filesUtilities.isVideoPath(it)
            })
        }
    }

    private fun isDecryptAction(): Boolean = viewAction.value == MediaViewAction.DECRYPT_MEDIA

    private fun isEncryptAction(): Boolean = viewAction.value.let { action ->
        action == MediaViewAction.PICK_MEDIA ||
                action == MediaViewAction.TAKE_MEDIA ||
                action == MediaViewAction.ENCRYPT_MEDIA
    }

    override fun onCleared() {
        filesUtilities.deleteCacheDir()
        super.onCleared()
    }

    fun removeSelectedFromList(path: String, showMessage: Boolean = true) {
        viewModelScope.launch {
            _selectedMedias.emit(_selectedMedias.value.filter { it.path != path })
            if (showMessage) {
                _messageFlow.emit(R.string.file_removed_from_list)
            }
            if (selectedMediasFlow.value.isEmpty()) {
                _latestAction.emit(MediaViewAction.DEFAULT)
            } else {
                _mediaViewState.emit(
                    MediaViewState.EncryptDecryptState(
                        selectedMediasFlow.value,
                        getOnActionClickedCallback()
                    )
                )
            }
        }
    }

    fun deleteSelectedFromList(path: String, isEncrypted: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            try {
                if (!isEncrypted) {
                    mediaStoreManager.deleteFilesFromExternalStorageAndMediaStore(
                        listOf(path)
                    )
                    _messageFlow.emit(R.string.file_deleted_from_external_storage)
                } else {
                    val thumb = filesUtilities.getStableEncryptedThumbPathForDecryptedThumb(
                        filesUtilities.getNameOfFileWithExtension(path)
                    )
                    filesRepository.getFileByThumbPath(
                        thumb
                    )?.also {
                        filesRepository.deleteEncryptedFilesFromKryptDBAndFileSystem(
                            listOf(it)
                        )
                        File(path).delete()
                        File(thumb).delete()
                        _messageFlow.emit(R.string.file_deleted_from_krypt_storage)
                    }
                }
                removeSelectedFromList(path, showMessage = false)
            } catch (t: Throwable) {
                _messageFlow.emit(R.string.something_went_wrong)
            }
        }
    }

    fun deleteAllSelectedFiles() {
        viewModelScope.launch(ioDispatcher) {

            suspend fun closeMedia() {
                _selectedMedias.emit(listOf())
                _latestAction.emit(MediaViewAction.DEFAULT)
            }

            _mediaViewState.emit(MediaViewState.OperationStart)
            try {
                if (isEncryptAction()) {
                    selectedMediasFlow.value.also { selectedMedias ->
                        mediaStoreManager.deleteFilesFromExternalStorageAndMediaStore(
                            selectedMedias.map { it.path }
                        )
                    }
                    _messageFlow.emit(R.string.all_selected_file_deleted)
                    closeMedia()
                } else if (isDecryptAction()) {
                    filesRepository.mapThumbnailsAndNameToFileEntity(
                        selectedMediasFlow.value.map {
                            it.path
                        }.toTypedArray()
                    ).also {
                        filesRepository.deleteEncryptedFilesFromKryptDBAndFileSystem(it)
                    }
                    _messageFlow.emit(R.string.all_selected_file_deleted_from_krypt)
                    closeMedia()
                } else {
                    closeMedia()
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                _messageFlow.emit(R.string.something_went_wrong)
                closeMedia()
            }
        }
    }

    fun getDefaultFalleryOptions(): FalleryOptions =
        falleryBuilderProvider.getDefaultFalleryOptions()

    fun getKryptFalleryOptions(): FalleryOptions =
        falleryBuilderProvider.getMediaPickerForDecrypting()
}