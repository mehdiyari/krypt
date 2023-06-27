package ir.mehdiyari.krypt.ui.voice.audios

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.voice.player.MusicPlayerEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


@Composable
fun AddNewVoiceButton(
    modifier: Modifier,
    onNavigateToRecordAudio: () -> Unit,
) {
    ExtendedFloatingActionButton(
        modifier = modifier.padding(16.dp),
        onClick = {
            onNavigateToRecordAudio()
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_audio_24),
                contentDescription = "",
                tint = Color.White
            )
        },
        text = {
            Text(text = stringResource(id = R.string.add_new_audio))
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioList(
    modifier: Modifier,
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
    musicPlayerBottomSheetState: SheetState
) {
    val playingAudioState = currentAudioPlaying.collectAsState()
    LazyColumn(
        modifier = modifier,
        content = {
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
        }, contentPadding = PaddingValues(top = 8.dp, bottom = 70.dp, start = 6.dp, end = 6.dp)
    )
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
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
    musicPlayerBottomSheetState: SheetState
) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                onActionClicked.invoke(audioEntity)
                scope.launch { musicPlayerBottomSheetState.show() }
            }) {
                val playPauseIcon =
                    if (playingAudioState.value?.id == audioEntity.id && musicPlayerBottomSheetState.currentValue == SheetValue.Expanded) {
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