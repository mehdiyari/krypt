package ir.mehdiyari.krypt.mediaPlayer

sealed class PlayerState {
    data class NormalVideo(val path: String) : PlayerState()
    data class EncryptedCashedVideo(val path: String) : PlayerState()
    object ForceClose : PlayerState()
    object Decrypting : PlayerState()
}