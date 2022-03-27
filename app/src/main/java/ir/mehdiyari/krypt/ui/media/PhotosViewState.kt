package ir.mehdiyari.krypt.ui.media

sealed class PhotosViewState {

    data class EncryptDecryptState(
        val selectedPhotosCount: Int = 1,
        val onEncryptOrDecryptAction: (deleteAfterEncryption: Boolean) -> Unit,
    ) : PhotosViewState()

    object Default : PhotosViewState()

    object OperationStart : PhotosViewState()
    object OperationFinished : PhotosViewState()
    object OperationFailed : PhotosViewState()
}