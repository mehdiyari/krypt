package ir.mehdiyari.krypt.mediaPlayer

import android.content.Context
import android.content.Intent
import androidx.media3.exoplayer.ExoPlayer

internal object PlayerFactory {

    fun getNormalStreamPlayer(activity: Context): ExoPlayer = ExoPlayer.Builder(activity)
        .build()

    fun getEncryptedStreamPlayer(activity: Context): ExoPlayer = TODO("Not Implemented YET")
}