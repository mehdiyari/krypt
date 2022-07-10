package ir.mehdiyari.krypt.ui.media.player

import android.content.Context
import android.os.Bundle
import com.google.android.exoplayer2.ExoPlayer

object PlayerFactory {

    fun getNormalStreamPlayer(activity: Context): ExoPlayer = ExoPlayer.Builder(activity)
        .build()

    fun getEncryptedStreamPlayer(activity: Context): ExoPlayer = TODO("Not Implemented YET")
}

fun createBundleForPlayer(videoPath: String, isEncryptedVideo: Boolean = false): Bundle =
    Bundle().apply {
        this.putString("video", videoPath)
        this.putBoolean("encrypted", isEncryptedVideo)
    }