package ir.mehdiyari.krypt.ui.text.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.di.qualifiers.AccountName
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.ui.text.list.TextEntity
import ir.mehdiyari.krypt.utils.TextFilesUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class AddTextViewModel @Inject constructor(
    private val textFilesUtils: TextFilesUtils,
    private val filesRepository: FilesRepository,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    @AccountName private val accountName: Provider<String>
) : ViewModel() {

    private val _saveNoteState = MutableStateFlow<Boolean?>(null)
    val saveNoteState: StateFlow<Boolean?> = _saveNoteState

    private val _saveNoteValidation = MutableStateFlow<Int?>(null)
    val saveNoteValidation: StateFlow<Int?> = _saveNoteValidation

    private val _argsTextViewState = MutableStateFlow<AddTextArgsViewState?>(null)
    val argsTextViewState: StateFlow<AddTextArgsViewState?> = _argsTextViewState

    fun saveNote(title: String, content: String) {
        viewModelScope.launch(ioDispatcher) {
            if (title.trim().isEmpty()) {
                _saveNoteValidation.emit(R.string.title_must_not_empty)
                return@launch
            }

            if (content.isEmpty()) {
                _saveNoteValidation.emit(R.string.content_must_not_empty)
                return@launch
            }

            textFilesUtils.mapTitleAndContentToFile(title, content).also { textFile ->
                val encryptedFileResult = textFilesUtils.encryptTextFiles(textFile)
                if (encryptedFileResult.first) {
                    textFile.delete()
                    filesRepository.insertFiles(
                        listOf(
                            FileEntity(
                                type = FileTypeEnum.Text,
                                filePath = encryptedFileResult.second!!,
                                metaData = textFilesUtils.getEncryptedBase64MetaDataFromTitleAndContent(
                                    title,
                                    content
                                ) ?: "",
                                accountName = accountName.get()
                            )
                        )
                    )

                    _saveNoteState.emit(true)
                } else {
                    _saveNoteState.emit(false)
                }
            }
        }
    }

    fun handleInputTextID(textId: Long) {
        if (textId != -1L) {
            viewModelScope.launch(ioDispatcher) {
                filesRepository.getFileById(textId).also {
                    handleInputTextFileEntity(it, textId)
                }
            }
        }
    }

    private suspend fun handleInputTextFileEntity(
        it: FileEntity?,
        textId: Long
    ) {
        if (it == null) {
            _argsTextViewState.emit(
                AddTextArgsViewState.Error(R.string.cant_find_these_text)
            )
        } else {
            textFilesUtils.decryptTextFile(it.filePath).also { titleContentPair ->
                if (titleContentPair != null) {
                    _argsTextViewState.emit(
                        AddTextArgsViewState.TextArg(
                            TextEntity(
                                textId,
                                titleContentPair.first,
                                titleContentPair.second
                            )
                        )
                    )
                } else {
                    _argsTextViewState.emit(
                        AddTextArgsViewState.Error(R.string.error_while_decrypting)
                    )
                }
            }
        }
    }
}