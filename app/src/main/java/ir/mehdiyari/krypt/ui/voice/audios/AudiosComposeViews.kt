package ir.mehdiyari.krypt.ui.voice.audios

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.voice.player.MusicPlayerBottomSheet
import ir.mehdiyari.krypt.ui.voice.player.MusicPlayerEntity
import ir.mehdiyari.krypt.ui.voice.player.MusicPlayerViewModel
import ir.mehdiyari.krypt.utils.KryptTheme
import ir.mehdiyari.krypt.utils.getAnimationNavUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AudiosScreen(
    navController: NavController? = null,
    audiosViewModel: AudiosViewModel = viewModel(),
    musicPlayerViewModel: MusicPlayerViewModel
) {
    val musicPlayerBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    KryptTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.audios_library))
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController?.navigateUp()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "")
                        }
                    }
                )
            }
        ) {
            AudioList(
                audiosViewModel.audios.collectAsState(),
                musicPlayerViewModel.currentAudioPlaying,
                musicPlayerViewModel::onAudioAction,
                musicPlayerBottomSheetState,
            )
        }

        AddNewVoiceButton(navController)
        val playingAudio = musicPlayerViewModel.currentAudioPlaying.collectAsState()
        val sliderState = remember {
            mutableStateOf(playingAudio.value?.currentValue ?: 0L)
        }
        MusicPlayerBottomSheet(
            musicPlayerBottomSheetState,
            playingAudio.value?.title ?: "",
            sliderState,
            playingAudio.value?.duration ?: 0L,
            musicPlayerViewModel::onPrevClicked,
            musicPlayerViewModel::onNextClicked,
            musicPlayerViewModel::onPlayPauseClicked,
        )
    }
}

@Preview
@Composable
fun AddNewVoiceButton(
    navController: NavController? = null
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(15.dp)
    ) {
        ExtendedFloatingActionButton(
            onClick = {
                navController
                    ?.navigate(
                        resId = R.id.action_audiosFragment_to_recordVoiceFragment,
                        args = null,
                        navOptions = getAnimationNavUtils()
                    )
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_audio_24),
                    contentDescription = ""
                )
            },
            text = {
                Text(text = stringResource(id = R.string.add_new_audio))
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AudioList(
    audios: State<List<AudioEntity>> = mutableStateOf(
        listOf(
            AudioEntity(1L, "Voice #60", "08:30", "2023/01/26 18:00:00"),
            AudioEntity(2L, "Voice #61", "23:03", "2023/12/10 19:11:00"),
            AudioEntity(3L, "Voice #62", "16:30", "2022/12/12 10:00:00")
        )
    ),
    currentAudioPlaying: StateFlow<MusicPlayerEntity?> = MutableStateFlow(
        MusicPlayerEntity(
            1L,
            "Voice #60",
            5646,
            54,
        )
    ),
    onActionClicked: (AudioEntity) -> Unit = {},
    musicPlayerBottomSheetState: ModalBottomSheetState
) {
    val playingAudioState = currentAudioPlaying.collectAsState()
    LazyColumn(content = {
        items(audios.value.size, key = {
            audios.value[it].id
        }) {
            AudioItem(
                audios.value[it],
                playingAudioState,
                onActionClicked,
                musicPlayerBottomSheetState
            )
        }
    }, contentPadding = PaddingValues(top = 8.dp, bottom = 70.dp, start = 6.dp, end = 6.dp))
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AudioItem(
    audioEntity: AudioEntity = AudioEntity(
        1L, "Voice #60", "08:30", "2023/01/26 09:00:00"
    ),
    playingAudioState: State<MusicPlayerEntity?> = mutableStateOf(
        MusicPlayerEntity(
            1L,
            "Voice #60",
            5646,
            54,
        )
    ),
    onActionClicked: (AudioEntity) -> Unit = {},
    musicPlayerBottomSheetState: ModalBottomSheetState
) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onActionClicked.invoke(audioEntity)
                scope.launch {
                    musicPlayerBottomSheetState.show()
                }
            }) {
                val playPauseIcon =
                    if (playingAudioState.value?.id == audioEntity.id && musicPlayerBottomSheetState.currentValue == ModalBottomSheetValue.Expanded) {
                        painterResource(id = R.drawable.ic_pause)
                    } else {
                        painterResource(id = R.drawable.ic_audio_play)
                    }
                Icon(
                    painter = playPauseIcon,
                    contentDescription = ""
                )
            }

            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (columnRef, dateText) = createRefs()
                Column(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .constrainAs(columnRef) {}
                ) {
                    Text(text = audioEntity.name, fontWeight = FontWeight.Bold)
                    Text(text = audioEntity.duration, fontSize = 12.sp)
                }

                Text(text = audioEntity.dateTime, fontSize = 10.sp, modifier = Modifier
                    .padding(end = 4.dp)
                    .constrainAs(dateText) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    })
            }

        }
    }
}