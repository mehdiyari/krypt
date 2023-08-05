package ir.mehdiyari.krypt.voice.shared.entity

data class AudioEntity(
    val id: Long,
    val name: String,
    val duration: String,
    val dateTime: String
) {
    companion object {
        const val NAME_PREFIX = "Voice #"
    }
}