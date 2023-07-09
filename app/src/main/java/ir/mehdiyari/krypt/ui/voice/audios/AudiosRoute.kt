package ir.mehdiyari.krypt.ui.voice.audios

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.voice.player.MusicPlayerBottomSheet
import ir.mehdiyari.krypt.ui.voice.player.MusicPlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudiosRoute(
    modifier: Modifier,
    audiosViewModel: AudiosViewModel = hiltViewModel(),
    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onNavigateToRecordAudio: () -> Unit,
) {
    audiosViewModel.getAudios()
    val playingAudio = musicPlayerViewModel.currentAudioPlaying.collectAsStateWithLifecycle()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        var openMusicPlayerBottomSheet by rememberSaveable { mutableStateOf(false) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.audios_library))
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onBackPressed()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "")
                        }
                    }
                )
            }
        ) {
            val audiosState = audiosViewModel.audios.collectAsStateWithLifecycle()
            AudioList(
                modifier = modifier,
                audiosState,
                playingAudio,
                musicPlayerViewModel::onAudioAction,
                { openMusicPlayerBottomSheet = true },
                topPadding = it.calculateTopPadding(),
                openMusicPlayerBottomSheet
            )
        }

        AddNewVoiceButton(modifier = modifier.align(Alignment.BottomEnd), onNavigateToRecordAudio)

        if (openMusicPlayerBottomSheet) {
            MusicPlayerBottomSheet(
                modifier = modifier,
                title = playingAudio.value?.title ?: "",
                sliderState = musicPlayerViewModel.getMusicTimeEmitter(),
                maxValue = playingAudio.value?.duration ?: 0L,
                onPrevClicked = musicPlayerViewModel::onPrevClicked,
                onNextClicked = musicPlayerViewModel::onNextClicked,
                onPlayPauseClicked = musicPlayerViewModel::onPlayPauseClicked,
                onSeekTo = musicPlayerViewModel::onSeekTo
            ) {
                musicPlayerViewModel.onCloseMediaPlayer()
                openMusicPlayerBottomSheet = false
            }
        }

    }
}