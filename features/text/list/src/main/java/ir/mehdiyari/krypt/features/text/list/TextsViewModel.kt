package ir.mehdiyari.krypt.features.text.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.dispatchers.di.DispatchersQualifierType
import ir.mehdiyari.krypt.dispatchers.di.DispatchersType
import ir.mehdiyari.krypt.features.text.logic.TextEntity
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.features.text.logic.TextFilesUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextsViewModel @Inject constructor(
    private val filesRepository: FilesRepository,
    @DispatchersType(DispatchersQualifierType.IO) private val ioDispatcher: CoroutineDispatcher,
    private val textFilesUtils: TextFilesUtils
) : ViewModel() {

    private val _textFilesList = MutableStateFlow<List<TextEntity>>(listOf())
    val textFilesList = _textFilesList.asStateFlow()

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