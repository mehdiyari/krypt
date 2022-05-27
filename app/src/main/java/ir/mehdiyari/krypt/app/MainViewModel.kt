package ir.mehdiyari.krypt.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.data.repositories.CurrentUser
import ir.mehdiyari.krypt.data.repositories.SettingsRepository
import ir.mehdiyari.krypt.ui.settings.AutoLockItemsEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val currentUser: CurrentUser
) : ViewModel() {

    private val _automaticLockState = MutableStateFlow(false)
    val automaticLockState: StateFlow<Boolean> = _automaticLockState
    private var lockerTimerJob: Job? = null

    fun onStartLocker() {
        releaseTimer()
        val autoLockValue = settingsRepository.getLockAutomaticallyValue()
        if (autoLockValue != AutoLockItemsEnum.Disabled) {
            lockerTimerJob = viewModelScope.launch(Dispatchers.Default) {
                delay(autoLockValue.value * 1000L)
                currentUser.clear()
                _automaticLockState.emit(true)
            }
        }
    }

    fun onStopLocker() {
        releaseTimer()
    }

    private fun releaseTimer() {
        lockerTimerJob?.cancel()
        lockerTimerJob = null
    }
}