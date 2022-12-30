package ir.mehdiyari.krypt.ui.voice.recorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ViewModelComponent::class)
@Module
class VoiceRecorderModule {

    @Provides
    fun provideMediaRecorder(
        @ApplicationContext context: Context
    ): MediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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

}