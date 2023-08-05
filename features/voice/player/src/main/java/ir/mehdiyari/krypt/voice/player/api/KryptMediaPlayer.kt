package ir.mehdiyari.krypt.voice.player.api

import kotlinx.coroutines.flow.StateFlow

interface KryptMediaPlayer {

    fun start(path: String, duration: Int)

    fun resume()

    fun pause()

    fun stop()

    fun isPlaying(): Boolean

    fun release()

    fun seekTo(value: Long)

    fun getMusicTimeEmitter(): StateFlow<Long>
}