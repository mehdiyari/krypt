package ir.mehdiyari.krypt.ui.voice.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.ui.voice.audios.AudioEntity
import ir.mehdiyari.krypt.ui.voice.recorder.meta.AudioMetaDataJsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val filesRepository: FilesRepository,
    private val audioMetaDataJsonAdapter: AudioMetaDataJsonParser,
) : ViewModel() {

    private val _currentAudioPlaying = MutableStateFlow<MusicPlayerEntity?>(null)
    val currentAudioPlaying = _currentAudioPlaying.asStateFlow()

    fun onAudioAction(audioEntity: AudioEntity) {
        viewModelScope.launch {
            _currentAudioPlaying.value = filesRepository.getAudioById(audioEntity.id)
                ?.mapToMusicPlayerEntity(audioEntity.name)
        }
    }

    private fun FileEntity.mapToMusicPlayerEntity(name: String): MusicPlayerEntity? {
        val meta = audioMetaDataJsonAdapter.fromJson(this.metaData)

        return MusicPlayerEntity(
            this.id, name, meta?.duration ?: 0L, 0L
        )
    }

    fun onPrevClicked() {
        // TODO("Not yet implemented")
    }

    fun onNextClicked() {
        // TODO("Not yet implemented")
    }

    fun onPlayPauseClicked() {
        //TODO("Not yet implemented")
    }
}

