package ir.mehdiyari.krypt.ui.voice.player

import android.media.MediaPlayer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class KryptMediaPlayerImpl @Inject constructor(
    private val mediaPlayer: MediaPlayer,
) : KryptMediaPlayer {

    private var mediaTimerJob: Job? = null
    private val _mediaPlayerTimeState = MutableStateFlow(0L)
    private val mediaPlayerTimeState = _mediaPlayerTimeState.asStateFlow()
    private var currentSoundDuration = 0

    override fun start(path: String, duration: Int) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(path)
        mediaPlayer.isLooping = true
        mediaPlayer.setOnPreparedListener {
            it.start()
            startMediaTimer(duration)
        }
        mediaPlayer.prepare()
        currentSoundDuration = duration
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startMediaTimer(duration: Int) {
        if (mediaTimerJob?.isActive == true) return

        mediaTimerJob = GlobalScope.launch {
            while (true) {
                ensureActive()
                delay(1000)
                if (_mediaPlayerTimeState.value >= duration) {
                    _mediaPlayerTimeState.value = 0
                } else {
                    _mediaPlayerTimeState.value += 1
                }
            }
        }
    }

    private fun stopMediaTimer() {
        mediaTimerJob?.cancel()
        mediaTimerJob = null
    }

    override fun resume() {
        startMediaTimer(currentSoundDuration)
        mediaPlayer.start()
    }

    override fun pause() {
        stopMediaTimer()
        mediaPlayer.pause()
    }

    override fun stop() {
        _mediaPlayerTimeState.value = 0L
        stopMediaTimer()
        mediaPlayer.stop()
    }

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun release() {
        mediaPlayer.stop()
        _mediaPlayerTimeState.value = 0L
        mediaPlayer.release()
        stopMediaTimer()
    }

    override fun seekTo(value: Long) {
        _mediaPlayerTimeState.value = value
        mediaPlayer.seekTo(value.toInt() * 1000)
    }

    override fun getMusicTimeEmitter(): StateFlow<Long> = mediaPlayerTimeState
}