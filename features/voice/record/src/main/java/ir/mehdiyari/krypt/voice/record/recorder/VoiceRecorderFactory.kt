package ir.mehdiyari.krypt.voice.record.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


internal class VoiceRecorderFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun provideMediaRecorder(): MediaRecorder? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            }
        } else {
            MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            }
        }
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }

}