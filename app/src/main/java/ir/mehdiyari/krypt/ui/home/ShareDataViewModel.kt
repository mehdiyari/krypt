package ir.mehdiyari.krypt.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareDataViewModel @Inject constructor() : ViewModel() {

    private val _sharedData = MutableStateFlow<SharedDataState?>(null)
    val sharedData: StateFlow<SharedDataState?> = _sharedData

    fun handleSharedText(sharedText: String?) {
        if (!sharedText.isNullOrBlank()) {
            viewModelScope.launch {
                _sharedData.emit(SharedDataState.SharedText(sharedText))
            }
        }
    }

    fun clearSharedData() {
        viewModelScope.launch {
            _sharedData.emit(null)
        }
    }

    fun handleSharedImages(vararg images: Uri) {
        if (images.isNotEmpty()) {
            viewModelScope.launch {
                _sharedData.emit(SharedDataState.SharedImages(images.toList()))
            }
        }
    }
}