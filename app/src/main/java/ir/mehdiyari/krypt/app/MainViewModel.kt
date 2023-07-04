package ir.mehdiyari.krypt.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.app.user.CurrentUserManager
import ir.mehdiyari.krypt.data.repositories.SettingsRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherDefault
import ir.mehdiyari.krypt.ui.settings.AutoLockItemsEnum
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val currentUserManager: CurrentUserManager,
    @DispatcherDefault private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _restartAppMutableStateFlow = MutableStateFlow(false)
    val restartAppStateFlow: StateFlow<Boolean> = _restartAppMutableStateFlow
    private var lockerTimerJob: Job? = null

    fun onStartLocker() {
        if (currentUserManager.isUserAvailable()) {
            releaseTimer()
            val autoLockValue = settingsRepository.getLockAutomaticallyValue()
            if (autoLockValue != AutoLockItemsEnum.Disabled) {
                lockerTimerJob = viewModelScope.launch(defaultDispatcher) {
                    delay(autoLockValue.value * 1000L)
                    currentUserManager.clearCurrentUser()
                    _restartAppMutableStateFlow.emit(true)
                }
            }
        }
    }

    fun onStopLocker() {
        releaseTimer()
    }

    fun onLockMenuClicked() {
        viewModelScope.launch {
            onStopLocker()
            currentUserManager.clearCurrentUser()
            _restartAppMutableStateFlow.emit(true)
        }
    }

    private fun releaseTimer() {
        lockerTimerJob?.cancel()
        lockerTimerJob = null
    }

    override fun onCleared() {
        releaseTimer()
        super.onCleared()
    }
}