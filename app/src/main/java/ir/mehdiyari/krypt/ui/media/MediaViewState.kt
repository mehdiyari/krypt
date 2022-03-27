package ir.mehdiyari.krypt.ui.media

sealed class MediaViewState {

    data class EncryptDecryptState(
        val selectedMediasCount: Int = 1,
        val onEncryptOrDecryptAction: (deleteAfterEncryption: Boolean) -> Unit,
    ) : MediaViewState()

    object Default : MediaViewState()

    object OperationStart : MediaViewState()
    object OperationFinished : MediaViewState()
    object OperationFailed : MediaViewState()
}