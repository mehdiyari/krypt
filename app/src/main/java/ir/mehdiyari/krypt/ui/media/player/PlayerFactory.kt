package ir.mehdiyari.krypt.ui.media.player

import android.content.Context
import android.content.Intent
import com.google.android.exoplayer2.ExoPlayer

object PlayerFactory {

    fun getNormalStreamPlayer(activity: Context): ExoPlayer = ExoPlayer.Builder(activity)
        .build()

    fun getEncryptedStreamPlayer(activity: Context): ExoPlayer = TODO("Not Implemented YET")
}

fun Intent.addExtraForPlayerToIntent(videoPath: String, isEncryptedVideo: Boolean = false): Intent =
    this.apply {
        this.putExtra("video", videoPath)
        this.putExtra("encrypted", isEncryptedVideo)
    }