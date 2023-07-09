package ir.mehdiyari.krypt.ui.voice.player

data class MusicPlayerEntity(
    val id: Long,
    val title: String,
    val duration: Long,
    val currentValue: Long,
    val path: String? = null,
)