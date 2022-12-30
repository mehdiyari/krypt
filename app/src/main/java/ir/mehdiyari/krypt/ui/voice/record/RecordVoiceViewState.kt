package ir.mehdiyari.krypt.ui.voice.record

sealed class RecordVoiceViewState {

    object Initialize : RecordVoiceViewState()

    data class RecordStarted(
        val isResumed: Boolean = false
    ) : RecordVoiceViewState()

    object RecordSavedSuccessfully : RecordVoiceViewState()

    object RecordSavedFailed : RecordVoiceViewState()
}