package ir.mehdiyari.krypt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.app.user.CurrentUserManager
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.repositories.files.FilesRepository
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
    private val currentUserManager: CurrentUserManager
) : ViewModel() {

    private val _filesCounts = MutableStateFlow<List<HomeCardsModel>>(listOf())
    val filesCounts: StateFlow<List<HomeCardsModel>> = _filesCounts

    fun getHomeItems() {
        viewModelScope.launch(ioDispatcher) {
            _filesCounts.emit(mapFileTypeCountToHomeCardsModel())
        }
    }

    private suspend fun mapFileTypeCountToHomeCardsModel(): List<HomeCardsModel> {
        val homeCardList = mutableListOf<HomeCardsModel>()
        var mediaCount = -1L
        filesRepository.getAllFilesTypeCounts().forEach {
            when (it.first) {
                FileTypeEnum.Audio -> homeCardList.add(
                    HomeCardsModel(
                        R.drawable.ic_add_audio_24,
                        R.string.audios_library,
                        it.second
                    )
                )

                FileTypeEnum.Text -> homeCardList.add(
                    HomeCardsModel(
                        R.drawable.ic_editor_50,
                        R.string.texts_library,
                        it.second
                    )
                )

                else -> {
                    if (mediaCount == -1L) {
                        mediaCount = it.second
                    } else {
                        homeCardList.add(
                            HomeCardsModel(
                                R.drawable.ic_gallery_50,
                                R.string.medias_library,
                                mediaCount + it.second
                            )
                        )
                    }
                }
            }
        }

        return homeCardList
    }

    fun lockKrypt() {
        currentUserManager.clearCurrentUser()
    }
}