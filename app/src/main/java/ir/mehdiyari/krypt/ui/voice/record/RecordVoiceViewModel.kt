package ir.mehdiyari.krypt.ui.voice.record

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RecordVoiceViewModel @Inject constructor() : ViewModel() {

    private val _recordTimer = MutableStateFlow("00:00:00")
    val recordTimer = _recordTimer.asStateFlow()

    private val _recordVoiceViewState =
        MutableStateFlow<RecordVoiceViewState>(RecordVoiceViewState.Initialize)
    val recordVoiceViewState = _recordVoiceViewState.asStateFlow()

    private val _actionsButtonState = MutableStateFlow(
        RecordActionButtonsState(
            stop = true to ::onStopRecord,
            resume = false to ::onResumeRecord,
            save = true to ::onSaveRecord
        )
    )

    val actionsButtonState = _actionsButtonState.asStateFlow()

    fun startRecord() {
        _recordVoiceViewState.value = RecordVoiceViewState.RecordStarted()
    }

    private fun onSaveRecord() {
        TODO("save and change the _recordVoiceViewState")
    }

    private fun onResumeRecord() {
        _actionsButtonState.value = _actionsButtonState.value.copy(
            stop = true to ::onStopRecord,
            resume = false to ::onResumeRecord,
        )
    }

    private fun onStopRecord() {
        _actionsButtonState.value = _actionsButtonState.value.copy(
            stop = false to ::onStopRecord,
            resume = true to ::onResumeRecord,
        )
    }

    fun saveRecordRetry() {
        TODO("Not yet implemented")
    }
}