package ir.mehdiyari.krypt.ui.voice.player

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.android.exoplayer2.R
import ir.mehdiyari.krypt.utils.KryptTheme
import ir.mehdiyari.krypt.utils.convertToReadableTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerBottomSheet(
    modifier: Modifier,
    playerSheetState: SheetState = rememberModalBottomSheetState(),
    title: String = "Stairway to Heaven",
    sliderState: MutableState<Long> = mutableLongStateOf(300L),
    maxValue: Long = 896L,
    onPrevClicked: () -> Unit = {},
    onNextClicked: () -> Unit = {},
    onPlayPauseClicked: () -> Unit = {},
) {
    KryptTheme {
        ModalBottomSheet(modifier = modifier, sheetState = playerSheetState, content = {
            MusicPlayerView(
                modifier,
                title,
                sliderState,
                maxValue,
                onPrevClicked,
                onNextClicked,
                onPlayPauseClicked
            )
        }, onDismissRequest = {})
    }
}

@Composable
fun MusicPlayerView(
    modifier: Modifier,
    title: String,
    sliderState: MutableState<Long>,
    maxValue: Long,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onPlayPauseClicked: () -> Unit,
) {
    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val playState = remember { mutableStateOf(false) }
        RotateAnimationForMusicPlayer(modifier, playState)

        Text(
            text = title,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        ConstraintLayout(
            modifier = modifier.padding(8.dp)
        ) {
            val (startText, slider, endText) = createRefs()

            Text(
                text = sliderState.value.convertToReadableTime(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(end = 4.dp)
                    .constrainAs(startText) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(slider.start)
                    }
            )

            Slider(
                value = sliderState.value.toFloat(),
                valueRange = 0f..maxValue.toFloat(),
                onValueChange = {
                    sliderState.value = it.toLong()
                },
                modifier = modifier.constrainAs(slider) {
                    this.width = Dimension.preferredWrapContent
                    start.linkTo(startText.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(endText.start)
                })

            Text(
                text = maxValue.convertToReadableTime(),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(start = 4.dp)
                    .constrainAs(endText) {
                        end.linkTo(parent.end)
                        start.linkTo(slider.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConstraintLayout {
                val (prev, play, next) = createRefs()
                Image(
                    painter = painterResource(id = R.drawable.exo_controls_previous),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = modifier
                        .constrainAs(prev) {
                            end.linkTo(play.start)
                        }
                        .clickable {
                            onPrevClicked()
                        }
                )

                Image(
                    painter = if (playState.value)
                        painterResource(id = R.drawable.exo_controls_play)
                    else
                        painterResource(id = R.drawable.exo_controls_pause),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .constrainAs(play) {
                            start.linkTo(prev.end)
                        }
                        .clickable {
                            playState.value = playState.value.not()
                            onPlayPauseClicked()
                        }
                )

                Image(
                    painter = painterResource(id = R.drawable.exo_controls_next),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    modifier = modifier
                        .constrainAs(next) {
                            start.linkTo(play.end)
                        }
                        .clickable {
                            onNextClicked()
                        }
                )
            }
        }
    }
}

@Composable
fun RotateAnimationForMusicPlayer(
    modifier: Modifier,
    isPaused: MutableState<Boolean> = mutableStateOf(false)
) {
    var currentRotation by remember { mutableFloatStateOf(0f) }
    val rotation = remember { Animatable(currentRotation) }
    if (!isPaused.value) {
        LaunchedEffect(true) {
            rotation.animateTo(
                targetValue = currentRotation + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            ) {
                currentRotation = this.value
            }
        }
    } else {
        LaunchedEffect(true) {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = repeatable(
                    animation = tween(300, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart,
                    iterations = 1
                )
            ) {
                currentRotation = this.value
            }
        }
    }

    Image(
        painter = painterResource(id = ir.mehdiyari.krypt.R.drawable.ic_music_logo),
        contentDescription = "",
        modifier = modifier
            .size(100.dp)
            .rotate(rotation.value),
    )
}


@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun MusicPlayerViewPreview() {
    KryptTheme {
        MusicPlayerView(
            modifier = Modifier,
            title = "Stairway to Heaven",
            sliderState = mutableLongStateOf(300L),
            maxValue = 896L,
            onPrevClicked = {},
            onNextClicked = {},
            onPlayPauseClicked = {},
        )
    }
}