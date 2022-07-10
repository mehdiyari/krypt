package ir.mehdiyari.krypt.ui.media.player

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private val player: ExoPlayer by lazy {
        PlayerFactory.getNormalStreamPlayer(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenThePlayer()
        setContentView(ComposeView(this).apply {
            setContent {
                PlayerComposeView(player)
            }
        })

        val videoPath = MediaItem.fromUri(intent.getStringExtra("video") ?: "")
        val isEncryptedVideo = intent.getBooleanExtra("encrypted", false)
        if (isEncryptedVideo) {
            finish()
        }

        player.setMediaItem(videoPath)
        player.prepare()
        player.playWhenReady = true
    }

    private fun fullScreenThePlayer() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onPause() {
        player.pause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}