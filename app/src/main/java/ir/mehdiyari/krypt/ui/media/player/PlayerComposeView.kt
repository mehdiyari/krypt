package ir.mehdiyari.krypt.ui.media.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun PlayerComposeView(
    player: ExoPlayer? = null
) {
    KryptTheme {
        AndroidView(
            factory = {
                StyledPlayerView(it).apply {
                    this.player = player
                    this.setShowShuffleButton(false)
                    this.setShowSubtitleButton(false)
                    this.setShowNextButton(false)
                    this.setShowPreviousButton(false)
                    this.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_ALWAYS)
                    //this.set
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Black)
        )
    }
}