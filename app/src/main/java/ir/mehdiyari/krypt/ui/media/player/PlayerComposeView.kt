package ir.mehdiyari.krypt.ui.media.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PlayerComposeView(
    player: ExoPlayer? = null,
    decryptVideoPathState: StateFlow<PlayerState?>? = null
) {
    KryptTheme {
        when (decryptVideoPathState?.collectAsState()?.value) {
            is PlayerState.NormalVideo, is PlayerState.EncryptedCashedVideo -> {
                AndroidView(
                    factory = {
                        StyledPlayerView(it).apply {
                            this.player = player
                            this.setShowShuffleButton(false)
                            this.setShowSubtitleButton(false)
                            this.setShowNextButton(false)
                            this.setShowPreviousButton(false)
                            this.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_ALWAYS)
                            this.keepScreenOn = true
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(Color.Black)
                )
            }
            is PlayerState.Decrypting -> {
                ProgressBar(withText = true)
            }
            else -> {
                ProgressBar(withText = false)
            }
        }
    }
}

@Composable
private fun ProgressBar(withText: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(55.dp))
        if (withText) {
            Text(
                text = stringResource(id = R.string.decrypting_video_loading),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 20.dp),
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}