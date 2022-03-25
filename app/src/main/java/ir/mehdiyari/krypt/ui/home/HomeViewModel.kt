package ir.mehdiyari.krypt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.repositories.CurrentUser
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val filesRepository: FilesRepository,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val currentUser: CurrentUser
) : ViewModel() {

    private val _filesCounts = MutableStateFlow<List<HomeCardsModel>>(listOf())
    val filesCounts: StateFlow<List<HomeCardsModel>> = _filesCounts

    fun getHomeData() {
        viewModelScope.launch(ioDispatcher) {
            _filesCounts.emit(
                filesRepository.getAllFilesTypeCounts().map {
                    when (it.first) {
                        FileTypeEnum.Photo -> HomeCardsModel(
                            R.drawable.ic_gallery_50,
                            R.string.photos_library,
                            it.second
                        )
                        FileTypeEnum.Video -> HomeCardsModel(
                            R.drawable.ic_video_library_50,
                            R.string.videos_library,
                            it.second
                        )
                        FileTypeEnum.Audio -> HomeCardsModel(
                            R.drawable.ic_add_audio_24,
                            R.string.audios_library,
                            it.second
                        )
                        FileTypeEnum.Music -> HomeCardsModel(
                            R.drawable.ic_add_music_24,
                            R.string.musics_library,
                            it.second
                        )
                        FileTypeEnum.Text -> HomeCardsModel(
                            R.drawable.ic_editor_50,
                            R.string.texts_library,
                            it.second
                        )
                    }
                }
            )
        }
    }

    fun lockKrypt() {
        currentUser.clear()
    }
}