package ir.mehdiyari.krypt.ui.photo

sealed class PhotosViewState {

    data class EncryptDecryptState(
        val selectedPhotosCount: Int = 1,
        val onEncryptOrDecryptAction: (deleteAfterEncryption: Boolean) -> Unit,
    ) : PhotosViewState()

    object Default : PhotosViewState()
}