package ir.mehdiyari.krypt.ui.voice.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.repositories.files.FilesRepository
import ir.mehdiyari.krypt.ui.voice.audios.entity.AudioEntity
import ir.mehdiyari.krypt.ui.voice.recorder.meta.AudioMetaDataJsonParser
import ir.mehdiyari.krypt.utils.FilesUtilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val filesRepository: FilesRepository,
    private val audioMetaDataJsonAdapter: AudioMetaDataJsonParser,
    private val kryptMediaPlayer: KryptMediaPlayer,
    private val kryptCryptographyHelper: KryptCryptographyHelper,
    private val filesUtilities: FilesUtilities,
) : ViewModel() {

    private val _currentAudioPlaying = MutableStateFlow<MusicPlayerEntity?>(null)
    val currentAudioPlaying = _currentAudioPlaying.asStateFlow()
    private var currentAudioFilePath: String? = null

    fun onAudioAction(audioEntity: AudioEntity) {
        viewModelScope.launch {
            var fileEntity: FileEntity? = null
            _currentAudioPlaying.value = filesRepository.getAudioById(audioEntity.id)?.apply {
                fileEntity = this
            }?.mapToMusicPlayerEntity(audioEntity.name).also {
                if (fileEntity != null) {
                    decryptSelectedAudio(fileEntity!!, it?.duration?.toInt())
                }
            }
        }
    }

    private fun decryptSelectedAudio(fileEntity: FileEntity, duration: Int?) {
        viewModelScope.launch {
            val cachePathForAudio = filesUtilities.generateCacheAudioPath(
                File(fileEntity.filePath).name
            )

            val result = kryptCryptographyHelper.decryptFile(fileEntity.filePath, cachePathForAudio)
            if (result.isSuccess) {
                currentAudioFilePath = cachePathForAudio
                onAudioFileReady(cachePathForAudio, duration ?: 0)
            } else {
                result.exceptionOrNull()?.printStackTrace()
                onFailedToReadAudioFile()
            }
        }
    }

    private fun onFailedToReadAudioFile() {
        TODO("Not yet implemented")
    }

    private fun onAudioFileReady(cachePathForAudio: String, duration: Int) {
        kryptMediaPlayer.start(cachePathForAudio, duration)
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
        if (kryptMediaPlayer.isPlaying()) {
            kryptMediaPlayer.pause()
        } else {
            kryptMediaPlayer.resume()
        }
    }

    fun onSeekTo(value: Long) {
        kryptMediaPlayer.seekTo(value)
    }

    fun onCloseMediaPlayer() {
        runCatching { File(currentAudioFilePath ?: "").delete() }
        kryptMediaPlayer.stop()
    }

    override fun onCleared() {
        filesUtilities.deleteCacheDir()
        filesUtilities.deleteCachedAudioDIR()
        kryptMediaPlayer.release()
        super.onCleared()
    }

    fun getMusicTimeEmitter(): StateFlow<Long> = kryptMediaPlayer.getMusicTimeEmitter()
}

