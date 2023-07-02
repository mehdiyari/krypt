package ir.mehdiyari.krypt.ui.voice.recorder

import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.WorkerThread
import dagger.Lazy
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class VoiceRecorder @Inject constructor(
    private val mediaRecorder: Lazy<MediaRecorder?>,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private var isRecording: Boolean = false
    private var filePath = ""

    fun getMediaRecorderInstance(): MediaRecorder? = mediaRecorder.get()

    @WorkerThread
    suspend fun startRecord(path: String) = withContext(ioDispatcher) {
        filePath = path
        getMediaRecorderInstance()?.setOutputFile(path)
        getMediaRecorderInstance()?.prepare()
        getMediaRecorderInstance()?.start()
        isRecording = true
    }

    fun pauseRecord() {
        isRecording = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getMediaRecorderInstance()?.pause()
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getMediaRecorderInstance()?.resume()
            isRecording = true
        }
    }

    fun stopRecord() {
        isRecording = false
        getMediaRecorderInstance()?.stop()
        getMediaRecorderInstance()?.release()
    }

    fun deleteAudioCacheFile(): Boolean {
        return try {
            File(filePath).let {
                if (it.exists())
                    it.delete()
                else
                    true
            }
        } catch (ignored: Throwable) {
            false
        } finally {
            filePath = ""
        }
    }

    fun setOnErrorListener(listener: MediaRecorder.OnErrorListener) {
        getMediaRecorderInstance()?.setOnErrorListener(listener)
    }

    fun setOnInfoListener(listener: MediaRecorder.OnInfoListener) {
        getMediaRecorderInstance()?.setOnInfoListener(listener)
    }

    fun getRecordFilePath(): String = filePath

    fun isInRecordingState(): Boolean = isRecording
}