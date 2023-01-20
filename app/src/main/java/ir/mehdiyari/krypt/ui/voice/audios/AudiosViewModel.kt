package ir.mehdiyari.krypt.ui.voice.audios

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AudiosViewModel @Inject constructor(

) : ViewModel() {

    private val _audios = MutableStateFlow<List<AudioEntity>>(listOf())
    val audios = _audios.asStateFlow()

    private val _currentAudioPlaying = MutableStateFlow<AudioEntity?>(null)
    val currentAudioPlaying = _currentAudioPlaying.asStateFlow()


    fun onAudioAction(audioEntity: AudioEntity) {
        TODO()
    }
}