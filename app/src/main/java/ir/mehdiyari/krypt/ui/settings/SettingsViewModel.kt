package ir.mehdiyari.krypt.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(

) : ViewModel() {

    private val _automaticallyLockSelectedItem =
        MutableStateFlow(R.string.settings_lock_auto_disabled)
    val automaticallyLockSelectedItem: StateFlow<Int> = _automaticallyLockSelectedItem

    fun onSelectAutoLockItem(itemId: Int) {
        viewModelScope.launch {
            _automaticallyLockSelectedItem.emit(itemId)
        }
    }
}