package ir.mehdiyari.krypt.voice.record.record

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.voice.record.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ir.mehdiyari.krypt.shared.designsystem.resources.R as DesignSystemR

@Composable
internal fun AddAudioScreenContent(
    modifier: Modifier,
    onBackPressed: () -> Unit,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    recordVoiceViewState: StateFlow<RecordVoiceViewState>,
    onSaveRecordRetry: () -> Unit,
    startRecord: () -> Unit,
    recordTimerState: StateFlow<String>,
    actionButtonState: StateFlow<RecordActionButtonsState>,
) {
    val recordState = recordVoiceViewState.collectAsStateWithLifecycle()
    when (recordState.value) {
        is RecordVoiceViewState.NavigateUp -> {
            onBackPressed()
            return
        }

        is RecordVoiceViewState.MicError -> {
            Toast.makeText(
                LocalContext.current, R.string.microphone_error, Toast.LENGTH_LONG
            ).show()
            onBackPressed()
            return
        }

        is RecordVoiceViewState.RecordSavedSuccessfully -> {
            HandleSuccessState(onBackPressed)
            return
        }

        is RecordVoiceViewState.RecordSavedFailed -> {
            RecordRetrySnackbar(
                snackbarHostState,
                onSaveRecordRetry,
                onBackPressed,
                coroutineScope
            )
            return
        }

        else -> Unit
    }

    RecordButton(
        modifier, startRecord, recordState.value is RecordVoiceViewState.Initialize
    )

    RecordVoiceViews(
        modifier,
        timerStateFlow = recordTimerState,
        recordButtonsState = actionButtonState,
        recordUIVisibilityState = recordState.value is RecordVoiceViewState.RecordStarted,
        isPaused = (recordState.value as? RecordVoiceViewState.RecordStarted)?.isPaused
            ?: false
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
private fun RecordRetrySnackbar(
    snackbarHostState: SnackbarHostState,
    retry: () -> Unit,
    onBackPressed: () -> Unit,
    coroutineScope: CoroutineScope,
) {
    val title = stringResource(id = R.string.voice_recorded_failed)
    val action = stringResource(id = R.string.voice_recorder_retry)
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = title, actionLabel = action, duration = SnackbarDuration.Indefinite
        ).apply {
            if (this == SnackbarResult.ActionPerformed) {
                retry()
            } else if (this == SnackbarResult.Dismissed) {
                onBackPressed()
            }
        }
    }
}

@Composable
private fun HandleSuccessState(onBackPressed: () -> Unit) {
    Toast.makeText(
        LocalContext.current, R.string.voice_recorded_successfully, Toast.LENGTH_SHORT
    ).show()

    onBackPressed()
}

@Composable
private fun RotateAnimationForRecordImage(isPaused: Boolean) {
    var currentRotation by remember { mutableFloatStateOf(0f) }
    val rotation = remember { Animatable(currentRotation) }
    if (!isPaused) {
        LaunchedEffect(true) {
            rotation.animateTo(
                targetValue = currentRotation + 360f, animationSpec = infiniteRepeatable(
                    animation = tween(3500, easing = LinearEasing), repeatMode = RepeatMode.Restart
                )
            ) {
                currentRotation = this.value
            }
        }
    } else {
        LaunchedEffect(true) {
            rotation.animateTo(
                targetValue = 0f, animationSpec = repeatable(
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
        painter = painterResource(id = R.drawable.ic_microphone_record),
        contentDescription = "",
        modifier = Modifier
            .size(100.dp)
            .rotate(rotation.value),
    )
}

@Composable
private fun RecordVoiceViews(
    modifier: Modifier,
    timerStateFlow: StateFlow<String>,
    recordButtonsState: StateFlow<RecordActionButtonsState>,
    recordUIVisibilityState: Boolean,
    isPaused: Boolean,
) {
    AnimatedVisibility(visible = recordUIVisibilityState, enter = scaleIn(), exit = scaleOut()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RotateAnimationForRecordImage(isPaused)
            TimerText(modifier, timerStateFlow)
            RecordControlsButtons(modifier, recordButtonsState)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TimerText(
    modifier: Modifier,
    numberStateFlow: StateFlow<String>,
) {
    val value = numberStateFlow.collectAsState()
    AnimatedContent(targetState = value.value, transitionSpec = {
        EnterTransition.None togetherWith ExitTransition.None
    }, label = "") { target ->
        Text(
            modifier = modifier
                .padding(top = 20.dp, bottom = 10.dp)
                .animateEnterExit(
                    enter = scaleIn(), exit = scaleOut()
                ), text = target, fontSize = 16.sp, fontWeight = FontWeight.Bold
        )
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun RecordControlsButtons(
    modifier: Modifier,
    recordActionButtonsState: StateFlow<RecordActionButtonsState>,
) {
    val recordActionButtonsModel = recordActionButtonsState.collectAsState().value
    AnimatedContent(
        targetState = recordActionButtonsModel.hashCode(), transitionSpec = {
            EnterTransition.None togetherWith ExitTransition.None
        }, label = ""
    ) {
        Row {
            if (recordActionButtonsModel.stop.first) {
                RecordActionButton(
                    modifier.animateEnterExit(
                        enter = scaleIn(), exit = scaleOut()
                    ),
                    recordActionButtonsModel.stop.second,
                    R.drawable.ic_record_stoped,
                    R.string.record_pause,
                )
            }

            if (recordActionButtonsModel.resume.first) {
                RecordActionButton(
                    modifier.animateEnterExit(
                        enter = scaleIn(), exit = scaleOut()
                    ),
                    recordActionButtonsModel.resume.second,
                    R.drawable.ic_record_resumed,
                    R.string.record_resume,
                )
            }

            if (recordActionButtonsModel.save.first) {
                RecordActionButton(
                    modifier,
                    recordActionButtonsModel.save.second,
                    DesignSystemR.drawable.ic_save_as,
                    R.string.record_save
                )
            }
        }
    }

}

@Composable
private fun RecordActionButton(
    modifier: Modifier,
    onClick: (() -> Unit)?,
    @DrawableRes iconId: Int,
    @StringRes textId: Int,
) {
    Button(
        onClick = {
            onClick?.invoke()
        }, modifier = modifier.padding(10.dp), shape = CircleShape
    ) {
        Row {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )

            Text(
                text = stringResource(id = textId),
                modifier = modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}


@Composable
private fun RecordButton(
    modifier: Modifier,
    startRecordCallback: () -> Unit,
    visibility: Boolean
) {
    AnimatedVisibility(visible = visibility, enter = scaleIn(), exit = scaleOut()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = modifier.size(120.dp),
                onClick = { startRecordCallback.invoke() },
                shape = CircleShape
            ) {
                Text(
                    text = stringResource(id = R.string.record_audio), textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
@Preview
private fun RecordRetrySnackbarPreview() {
    KryptTheme {
        RecordRetrySnackbar(
            snackbarHostState = remember { SnackbarHostState() },
            retry = {},
            onBackPressed = {},
            coroutineScope = rememberCoroutineScope(),
        )
    }
}

@Composable
@Preview
private fun RotateAnimationForRecordImagePreview() {
    KryptTheme {
        RotateAnimationForRecordImage(isPaused = false)
    }
}

@Composable
@Preview
private fun RecordVoiceViewsPreview() {
    KryptTheme {
        RecordVoiceViews(
            modifier = Modifier,
            timerStateFlow = MutableStateFlow("00:00:00"),
            recordButtonsState = MutableStateFlow(
                RecordActionButtonsState()
            ),
            recordUIVisibilityState = false,
            isPaused = false
        )
    }
}

@Composable
private fun TimerTextPreview() {
    KryptTheme {
        TimerText(
            modifier = Modifier,
            numberStateFlow = MutableStateFlow("00:00:01"),
        )
    }
}

@Composable
@Preview
private fun RecordControlsButtonsPreview() {
    KryptTheme {
        RecordControlsButtons(
            modifier = Modifier,
            recordActionButtonsState = MutableStateFlow(
                RecordActionButtonsState()
            )
        )
    }
}

@Composable
@Preview
private fun RecordActionButtonPreview() {
    KryptTheme {
        RecordActionButton(
            modifier = Modifier,
            onClick = null,
            iconId = R.drawable.ic_record_stoped,
            textId = R.string.record_pause,
        )
    }
}

@Composable
@Preview
private fun RecordButtonPreview() {
    KryptTheme {
        RecordButton(
            modifier = Modifier,
            startRecordCallback = { /*TODO*/ },
            visibility = true,
        )
    }
}