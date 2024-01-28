package ir.mehdiyari.krypt.voice.record.record

internal sealed class RecordVoiceViewState {

    object Initialize : RecordVoiceViewState()

    data class RecordStarted(
        val isPaused: Boolean = false
    ) : RecordVoiceViewState()

    object RecordSavedSuccessfully : RecordVoiceViewState()

    object RecordSavedFailed : RecordVoiceViewState() {
        override fun equals(other: Any?): Boolean = false
    }

    object NavigateUp : RecordVoiceViewState()
    object MicError : RecordVoiceViewState()
}