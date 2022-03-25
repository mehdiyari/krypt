package ir.mehdiyari.krypt.ui.text.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.utils.TextFilesUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextsViewModel @Inject constructor(
    private val filesRepository: FilesRepository,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val textFilesUtils: TextFilesUtils
) : ViewModel() {

    private val _textFilesList = MutableStateFlow<List<TextEntity>>(listOf())
    val textFilesList: StateFlow<List<TextEntity>> = _textFilesList

    fun getTextFiles() {
        viewModelScope.launch(ioDispatcher) {
            filesRepository.getAllTextFiles().map {
                val metaDataText = it.metaData
                val titleContent: Pair<String, String>? = if (metaDataText.isNotBlank()) {
                    textFilesUtils.decryptMetaData(metaDataText)
                } else {
                    null
                }

                TextEntity(
                    it.id,
                    titleContent?.first ?: "",
                    titleContent?.second ?: ""
                )
            }.also {
                _textFilesList.emit(it)
            }
        }
    }
}