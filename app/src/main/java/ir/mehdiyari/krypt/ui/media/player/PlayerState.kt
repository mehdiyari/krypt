package ir.mehdiyari.krypt.ui.media.player

sealed class PlayerState {
    data class NormalVideo(val path: String) : PlayerState()
    data class EncryptedCashedVideo(val path: String) : PlayerState()
    object ForceClose : PlayerState()
    object Decrypting : PlayerState()
}