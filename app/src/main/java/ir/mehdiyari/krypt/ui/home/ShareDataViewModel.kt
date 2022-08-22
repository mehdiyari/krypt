package ir.mehdiyari.krypt.ui.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareDataViewModel @Inject constructor() : ViewModel() {

    private val _sharedData = MutableStateFlow<SharedDataState?>(null)
    val sharedData = _sharedData.asStateFlow()

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

    fun handleSharedMedias(vararg images: Uri) {
        if (images.isNotEmpty()) {
            viewModelScope.launch {
                _sharedData.emit(SharedDataState.SharedMedias(images.toList()))
            }
        }
    }
}