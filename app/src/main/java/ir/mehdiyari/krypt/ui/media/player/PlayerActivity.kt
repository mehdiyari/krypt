package ir.mehdiyari.krypt.ui.media.player

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private val viewModel by viewModels<PlayerViewModel>()

    private val player: ExoPlayer by lazy {
        PlayerFactory.getNormalStreamPlayer(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreenThePlayer()
        setContentView(ComposeView(this).apply {
            setContent {
                PlayerComposeView(player, viewModel.getPlayerViewStateState())
            }
        })

        collectPlayerState(savedInstanceState?.getLong("position"))

        val isEncryptedVideo = intent.getBooleanExtra("encrypted", false)
        if (isEncryptedVideo) {
            viewModel.onEncryptedVideoReceived(intent.getStringExtra("video")!!)
        } else {
            viewModel.normalVideo(intent.getStringExtra("video")!!)
        }
    }

    private fun collectPlayerState(position: Long? = null) {
        lifecycleScope.launch {
            viewModel.getPlayerViewStateState().collect {
                when (it) {
                    is PlayerState.EncryptedCashedVideo -> {
                        playVideo(MediaItem.fromUri(it.path), position)
                    }
                    is PlayerState.ForceClose -> {
                        Toast.makeText(
                            this@PlayerActivity,
                            R.string.cant_decrypt_video,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        finish()
                    }
                    is PlayerState.NormalVideo -> {
                        playVideo(MediaItem.fromUri(it.path), position)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun playVideo(videoPath: MediaItem, position: Long? = null) {
        player.setMediaItem(videoPath)
        player.prepare()
        player.playWhenReady = true
        if (position != null) {
            player.seekTo(position)
        }
    }

    private fun fullScreenThePlayer() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("position", player.currentPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        player.pause()
        super.onPause()
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}