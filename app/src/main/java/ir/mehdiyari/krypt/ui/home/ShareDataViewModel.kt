package ir.mehdiyari.krypt.ui.home

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
                sharedData.value
            }
        }
    }

    fun clearSharedData() {
        viewModelScope.launch {
            _sharedData.emit(null)
        }
    }
}