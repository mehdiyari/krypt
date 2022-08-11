package ir.mehdiyari.krypt.ui.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.data.repositories.backup.BackupRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.utils.FilesUtilities
import ir.mehdiyari.krypt.utils.MediaStoreManager
import ir.mehdiyari.krypt.utils.formatSize
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val filesRepository: FilesRepository,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val filesUtilities: FilesUtilities,
    private val mediaStoreManager: MediaStoreManager,
) : ViewModel() {

    private val _filesSizes = MutableStateFlow("")
    val fileSizes = _filesSizes.asStateFlow()

    private val _lastBackupDateTime = MutableStateFlow<String?>(null)
    val lastBackupDateTime = _lastBackupDateTime.asStateFlow()

    private val _backupViewState = MutableStateFlow<BackupViewState?>(null)
    val backupViewState = _backupViewState.asStateFlow()

    private val _backups = MutableStateFlow<List<BackupViewData>>(listOf())
    val backups = _backups.asStateFlow()

    private val _generalMessageFlow = MutableSharedFlow<Int?>()
    val generalMessageFlow = _generalMessageFlow.asSharedFlow()

    private var backupJob: Job? = null

    init {
        refreshAllData()
    }

    private fun refreshAllData() {
        getAllFileSizes()
        getLastBackupDateTime()
        getBackups()
    }


    fun backupNow() {
        if (backupJob == null) {
            backupJob = viewModelScope.launch(ioDispatcher) {
                _backupViewState.emit(BackupViewState.Started)
                try {
                    if (backupRepository.backupAll()) {
                        _backupViewState.emit(BackupViewState.Finished)
                        refreshAllData()
                    } else {
                        _backupViewState.emit(BackupViewState.Failed(0))
                    }

                    backupJob = null
                } catch (t: Throwable) {
                    t.printStackTrace()
                    backupJob = null

                    if (t is CancellationException) {
                        _backupViewState.emit(BackupViewState.Canceled)
                    } else {
                        _backupViewState.emit(BackupViewState.Failed(0))
                    }
                }
            }
        }
    }

    fun onSaveBackup(backupFileId: Int) {
        viewModelScope.launch(ioDispatcher) {
            try {
                backupRepository.getBackupFilePathWithId(backupFileId).also {
                    filesUtilities.copyBackupFileToKryptFolder(it).also { path ->
                        if (path.isNotBlank()) {
                            try {
                                mediaStoreManager.scanAddedMedia(listOf(path))
                            } catch (t: Throwable) {
                                t.printStackTrace()
                            }
                        }
                    }
                }

                _generalMessageFlow.emit(R.string.save_backup_file_successfylly)
            } catch (t: Throwable) {
                if (t is SecurityException) {
                    _generalMessageFlow.emit(R.string.saving_backup_permission_error)
                } else {
                    _generalMessageFlow.emit(R.string.error_while_saving_backup_into_external_storage)
                }
            }
        }
    }

    fun onDeleteBackup(backupFileId: Int) {
        viewModelScope.launch(ioDispatcher) {
            try {
                backupRepository.deleteBackupWithId(backupFileId)
                _generalMessageFlow.emit(R.string.delete_backup_file_successfully)
            } catch (t: Throwable) {
                t.printStackTrace()
                _generalMessageFlow.emit(R.string.delete_backup_file_failed)
            }
            refreshAllData()
        }
    }

    private fun getBackups() {
        viewModelScope.launch(ioDispatcher) {
            _backups.emit(backupRepository.getBackupRecord()
                .map {
                    BackupViewData(
                        it.id,
                        backupRepository.convertToBackUpDateTimeFormat(it.dateTime),
                        formatSize(File(it.filePath).length())
                    )
                })
        }
    }

    private fun getAllFileSizes() {
        viewModelScope.launch(ioDispatcher) {
            _filesSizes.emit(formatSize(filesRepository.getAllFilesSize()))
        }
    }

    private fun getLastBackupDateTime() {
        viewModelScope.launch(ioDispatcher) {
            _lastBackupDateTime.emit(backupRepository.getLastBackUpDateTime())
        }
    }

    fun cancelBackup() {
        backupJob?.cancel()
        backupJob = null
    }

    override fun onCleared() {
        super.onCleared()
        backupJob?.cancel()
        backupJob = null
    }
}
