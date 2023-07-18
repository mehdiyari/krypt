package ir.mehdiyari.krypt.ui.text.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.crypto.impl.TextFilesUtils
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.repositories.files.FilesRepository
import ir.mehdiyari.krypt.dispatchers.di.DispatchersQualifierType
import ir.mehdiyari.krypt.dispatchers.di.DispatchersType
import ir.mehdiyari.krypt.ui.text.list.TextEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTextViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val textFilesUtils: TextFilesUtils,
    private val filesRepository: FilesRepository,
    @DispatchersType(DispatchersQualifierType.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val addTextArgs = AddTextArgs(savedStateHandle)

    private val _saveNoteState = MutableStateFlow<Boolean?>(null)
    val saveNoteState = _saveNoteState.asStateFlow()

    private val _deleteNoteState = MutableStateFlow<Boolean?>(null)
    val deleteNoteState = _deleteNoteState.asStateFlow()

    private val _saveNoteValidation = MutableStateFlow<Int?>(null)
    val saveNoteValidation = _saveNoteValidation.asStateFlow()

    private val _argsTextViewState = MutableStateFlow<AddTextArgsViewState?>(null)
    val argsTextViewState = _argsTextViewState.asStateFlow()

    init {
        val textId = addTextArgs.textId
        if (textId != -1L) {
            viewModelScope.launch(ioDispatcher) {
                filesRepository.getFileById(textId).also {
                    handleInputTextFileEntity(it, textId)
                }
            }
        }
    }

    fun saveNote(title: String, content: String) {
        if (title.trim().isEmpty()) {
            _saveNoteValidation.value = R.string.title_must_not_empty
            return
        }

        if (content.isEmpty()) {
            _saveNoteValidation.value = R.string.content_must_not_empty
            return
        }

        if (argsTextViewState.value is AddTextArgsViewState.TextArg) {
            return updateNote(title, content)
        }

        viewModelScope.launch(ioDispatcher) {
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
                                accountName = ""
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

    private fun updateNote(title: String, content: String) {
        val textEntity = (argsTextViewState.value as? AddTextArgsViewState.TextArg)?.textEntity
        if (textEntity == null) {
            _argsTextViewState.value = AddTextArgsViewState.Error(R.string.something_went_wrong)
            return
        }

        viewModelScope.launch(ioDispatcher) {
            textFilesUtils.mapTitleAndContentToFile(title, content).also { textFile ->
                val encryptedFileResult = textFilesUtils.encryptTextFiles(textFile)
                if (encryptedFileResult.first) {
                    textFile.delete()
                    val localTextEntity = filesRepository.getFileById(textEntity.id)!!
                    filesRepository.updateFile(
                        localTextEntity.copy(
                            filePath = encryptedFileResult.second!!,
                            metaData = textFilesUtils.getEncryptedBase64MetaDataFromTitleAndContent(
                                title = title,
                                content = content,
                            ) ?: ""
                        )
                    )
                    _saveNoteState.emit(true)
                } else {
                    _saveNoteState.emit(false)
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

    fun deleteNote() {
        viewModelScope.launch(ioDispatcher) {
            argsTextViewState.value.also { state ->
                if (state is AddTextArgsViewState.TextArg) {
                    filesRepository.getFileById(state.textEntity.id)?.also {
                        filesRepository.deleteEncryptedFilesFromKryptDBAndFileSystem(
                            listOf(it)
                        )

                        _deleteNoteState.emit(true)
                    } ?: _deleteNoteState.emit(false)
                } else {
                    _deleteNoteState.emit(false)
                }
            }
        }
    }
}