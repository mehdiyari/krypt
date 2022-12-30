package ir.mehdiyari.krypt.ui.voice.recorder

import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.WorkerThread
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class VoiceRecorder @Inject constructor(
    private val mediaRecorder: MediaRecorder,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) {
    private var isRecording: Boolean = false
    private var filePath = ""

    @WorkerThread
    suspend fun startRecord(path: String) = withContext(ioDispatcher) {
        filePath = path
        mediaRecorder.setOutputFile(path)
        mediaRecorder.prepare()
        mediaRecorder.start()
        isRecording = true
    }

    fun pauseRecord() {
        isRecording = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder.pause()
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder.resume()
            isRecording = true
        }
    }

    fun stopRecord() {
        isRecording = false
        mediaRecorder.stop()
        mediaRecorder.release()
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
        mediaRecorder.setOnErrorListener(listener)
    }

    fun setOnInfoListener(listener: MediaRecorder.OnInfoListener) {
        mediaRecorder.setOnInfoListener(listener)
    }

    fun getRecordFilePath(): String = filePath

    fun isInRecordingState(): Boolean = isRecording
}