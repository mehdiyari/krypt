package ir.mehdiyari.krypt.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.data.repositories.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _automaticallyLockSelectedItem =
        MutableStateFlow(settingsRepository.getLockAutomaticallyValue())

    val automaticallyLockSelectedItem: StateFlow<AutoLockItemsEnum> = _automaticallyLockSelectedItem

    fun onSelectAutoLockItem(autoLockItemsEnum: AutoLockItemsEnum) {
        viewModelScope.launch {
            _automaticallyLockSelectedItem.emit(autoLockItemsEnum)
            settingsRepository.storeLockAutomatically(autoLockItemsEnum)
        }
    }
}