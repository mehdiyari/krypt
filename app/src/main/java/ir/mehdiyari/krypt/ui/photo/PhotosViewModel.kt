package ir.mehdiyari.krypt.ui.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor() : ViewModel() {

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
                        action == PhotosFragmentAction.PICK_PHOTO
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
        TODO()
    }

    private fun decrypt(
        photos: Array<String>,
        deleteAfterEncrypt: Boolean
    ) {
        TODO()
    }
}