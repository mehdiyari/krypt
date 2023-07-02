package ir.mehdiyari.krypt.ui.voice.audios

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.voice.audios.entity.AudioEntity
import ir.mehdiyari.krypt.ui.voice.player.MusicPlayerEntity
import ir.mehdiyari.krypt.utils.KryptTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioList(
    modifier: Modifier,
    audios: State<List<AudioEntity>>,
    currentAudioPlaying: State<MusicPlayerEntity?>,
    onActionClicked: (AudioEntity) -> Unit,
    musicPlayerBottomSheetState: SheetState,
    topPadding: Dp,
) {
    LazyColumn(
        modifier = modifier.padding(top = topPadding),
        content = {
            items(audios.value.size, key = {
                audios.value[it].id
            }) {
                AudioItem(
                    modifier,
                    audios.value[it],
                    currentAudioPlaying,
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
    modifier: Modifier,
    audioEntity: AudioEntity,
    playingAudioState: State<MusicPlayerEntity?>,
    onActionClicked: (AudioEntity) -> Unit,
    musicPlayerBottomSheetState: SheetState
) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = modifier
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
                modifier = modifier.fillMaxWidth()
            ) {
                val (columnRef, dateText) = createRefs()
                Column(
                    modifier = modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .constrainAs(columnRef) {}
                ) {
                    Text(text = audioEntity.name, fontWeight = FontWeight.Bold)
                    Text(text = audioEntity.duration, fontSize = 12.sp)
                }

                Text(text = audioEntity.dateTime, fontSize = 10.sp, modifier = modifier
                    .padding(end = 4.dp)
                    .constrainAs(dateText) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    })
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun AudioListPreview(
    @PreviewParameter(AudioPreviewParameterProvider::class) audios: List<AudioEntity>
) {
    KryptTheme {
        AudioList(
            modifier = Modifier,
            audios = mutableStateOf(audios),
            currentAudioPlaying = mutableStateOf(
                MusicPlayerEntity(
                    1L,
                    "Voice #60",
                    5646,
                    54,
                )
            ),
            onActionClicked = {},
            musicPlayerBottomSheetState = rememberModalBottomSheetState(),
            topPadding = 0.dp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun AudioItemPreview(
    @PreviewParameter(AudioPreviewParameterProvider::class) audios: List<AudioEntity>
) {
    KryptTheme {
        AudioItem(
            modifier = Modifier,
            audioEntity = audios[0],
            playingAudioState = mutableStateOf(
                MusicPlayerEntity(
                    1L,
                    "Voice #60",
                    5646,
                    54,
                )
            ),
            onActionClicked = {},
            musicPlayerBottomSheetState = rememberModalBottomSheetState()
        )
    }
}

internal class AudioPreviewParameterProvider :
    PreviewParameterProvider<List<AudioEntity>> {
    override val values: Sequence<List<AudioEntity>>
        get() = sequenceOf(
            listOf(
                AudioEntity(1L, "Voice #60", "08:30", "2023/01/26 18:00:00"),
                AudioEntity(2L, "Voice #61", "23:03", "2023/12/10 19:11:00"),
                AudioEntity(3L, "Voice #62", "16:30", "2022/12/12 10:00:00")
            )
        )
}