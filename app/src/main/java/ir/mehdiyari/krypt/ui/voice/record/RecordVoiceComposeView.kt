package ir.mehdiyari.krypt.ui.voice.record

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.navigation.NavController
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun RecordVoiceScreen(
    viewModel: RecordVoiceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController? = null,
) {
    KryptTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        RecordToolbar(navController, snackbarHostState) {
            val recordState = viewModel.recordVoiceViewState.collectAsState()

            when (recordState.value) {
                is RecordVoiceViewState.NavigateUp -> {
                    navController?.navigateUp()
                    return@RecordToolbar
                }
                is RecordVoiceViewState.MicError -> {
                    Toast.makeText(
                        LocalContext.current,
                        R.string.microphone_error,
                        Toast.LENGTH_LONG
                    ).show()
                    navController?.navigateUp()
                    return@RecordToolbar
                }
                is RecordVoiceViewState.RecordSavedSuccessfully -> {
                    HandleSuccessState(navController)
                    return@RecordToolbar
                }
                is RecordVoiceViewState.RecordSavedFailed -> {
                    RecordRetrySnackbar(
                        snackbarHostState,
                        viewModel::saveRecordRetry,
                        navController,
                        coroutineScope
                    )
                    return@RecordToolbar
                }
            }

            RecordButton(
                viewModel::startRecord,
                recordState.value is RecordVoiceViewState.Initialize
            )

            RecordUI(
                timerStateFlow = viewModel.recordTimer,
                recordButtonsState = viewModel.actionsButtonState,
                recordUIVisibilityState = recordState.value is RecordVoiceViewState.RecordStarted,
                isPaused = (recordState.value as? RecordVoiceViewState.RecordStarted)?.isPaused
                    ?: false
            )
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
@Preview
private fun RecordRetrySnackbar(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    retry: () -> Unit = {},
    navController: NavController? = null,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val title = stringResource(id = R.string.voice_recorded_failed)
    val action = stringResource(id = R.string.voice_recorder_retry)
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = title,
            actionLabel = action,
            duration = SnackbarDuration.Indefinite
        ).apply {
            if (this == SnackbarResult.ActionPerformed) {
                retry()
            } else if (this == SnackbarResult.Dismissed) {
                navController?.navigateUp()
            }
        }
    }
}

@Composable
private fun HandleSuccessState(navController: NavController?) {
    Toast.makeText(
        LocalContext.current,
        R.string.voice_recorded_successfully,
        Toast.LENGTH_SHORT
    ).show()

    navController?.navigateUp()
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@Preview
private fun RecordToolbar(
    navigateUpCallback: NavController? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable() (() -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.record_audio))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigateUpCallback?.navigateUp()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "")
                    }
                }
            )
        }, content = {
            content?.invoke()
        }, snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        })
}

@Composable
fun RotateAnimationForRecordImage(isPaused: Boolean = false) {
    var currentRotation by remember { mutableStateOf(0f) }
    val rotation = remember { Animatable(currentRotation) }
    if (!isPaused) {
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
        painter = painterResource(id = R.drawable.ic_microphone_record),
        contentDescription = "",
        modifier = Modifier
            .size(100.dp)
            .rotate(rotation.value),
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun RecordUI(
    timerStateFlow: StateFlow<String> = MutableStateFlow("00:00:00"),
    recordButtonsState: StateFlow<RecordActionButtonsState> = MutableStateFlow(
        RecordActionButtonsState()
    ),
    recordUIVisibilityState: Boolean = false,
    isPaused: Boolean = false
) {
    AnimatedVisibility(visible = recordUIVisibilityState, enter = scaleIn(), exit = scaleOut()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RotateAnimationForRecordImage(isPaused)
            TimerText(timerStateFlow)
            RecordControlsButtons(recordButtonsState)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun TimerText(numberStateFlow: StateFlow<String> = MutableStateFlow("00:00:01")) {
    val value = numberStateFlow.collectAsState()
    AnimatedContent(
        targetState = value.value,
        transitionSpec = {
            EnterTransition.None with ExitTransition.None
        }
    ) { target ->
        Text(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 10.dp)
                .animateEnterExit(
                    enter = scaleIn(),
                    exit = scaleOut()
                ),
            text = target,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun RecordControlsButtons(
    recordActionButtonsState: StateFlow<RecordActionButtonsState> = MutableStateFlow(
        RecordActionButtonsState()
    )
) {
    val recordActionButtonsModel = recordActionButtonsState.collectAsState().value
    AnimatedContent(
        targetState = recordActionButtonsModel.hashCode(),
        transitionSpec = {
            EnterTransition.None with ExitTransition.None
        }
    ) {
        Row {
            if (recordActionButtonsModel.stop.first) {
                RecordActionButton(
                    recordActionButtonsModel.stop.second,
                    R.drawable.ic_record_stoped,
                    R.string.record_pause,
                    Modifier
                        .animateEnterExit(
                            enter = scaleIn(),
                            exit = scaleOut()
                        )
                )
            }

            if (recordActionButtonsModel.resume.first) {
                RecordActionButton(
                    recordActionButtonsModel.resume.second,
                    R.drawable.ic_record_resumed,
                    R.string.record_resume,
                    Modifier
                        .animateEnterExit(
                            enter = scaleIn(),
                            exit = scaleOut()
                        )
                )
            }

            if (recordActionButtonsModel.save.first) {
                RecordActionButton(
                    recordActionButtonsModel.save.second,
                    R.drawable.ic_save_as,
                    R.string.record_save
                )
            }
        }
    }

}

@Composable
private fun RecordActionButton(
    onClick: (() -> Unit)? = null,
    @DrawableRes iconId: Int = R.drawable.ic_record_stoped,
    @StringRes textId: Int = R.string.record_pause,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onClick?.invoke()
        },
        modifier = modifier
            .padding(10.dp),
        shape = CircleShape
    ) {
        Row {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
            )

            Text(
                text = stringResource(id = textId),
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
@Preview
fun RecordButton(startRecordCallback: () -> Unit = {}, visibility: Boolean = true) {
    AnimatedVisibility(visible = visibility, enter = scaleIn(), exit = scaleOut()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier.size(120.dp),
                onClick = { startRecordCallback.invoke() },
                shape = CircleShape
            ) {
                Text(
                    text = stringResource(id = R.string.record_audio),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewRecord() {
    KryptTheme {
        RecordToolbar {
            RecordUI(
                recordUIVisibilityState = true
            )
        }
    }
}