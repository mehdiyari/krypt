package ir.mehdiyari.krypt.ui.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.data.repositories.backup.BackupRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val filesRepository: FilesRepository,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _filesSizes = MutableStateFlow(0L)
    val fileSizes = _filesSizes.asStateFlow()

    private val _lastBackupDateTime = MutableStateFlow<String?>(null)
    val lastBackupDateTime = _lastBackupDateTime.asStateFlow()

    private val _backupViewState = MutableStateFlow<BackupViewState?>(null)
    val backupViewState = _backupViewState.asStateFlow()

    private val _backups = MutableStateFlow<List<BackupViewData>>(listOf())
    val backups = _backups.asStateFlow()

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
        //TODO("Not yet implemented")
    }

    fun onDeleteBackup(backupFileId: Int) {
        //TODO("Not yet implemented")
    }

    private fun getBackups() {
        viewModelScope.launch(ioDispatcher) {
            _backups.emit(backupRepository.getBackupRecord()
                .map {
                    BackupViewData(
                        it.id,
                        backupRepository.convertToBackUpDateTimeFormat(it.dateTime),
                        getFileSizeAsMB(it.filePath)
                    )
                })
        }
    }

    private fun getAllFileSizes() {
        viewModelScope.launch(ioDispatcher) {
            _filesSizes.emit(filesRepository.getAllFilesSize())
        }
    }

    private fun getLastBackupDateTime() {
        viewModelScope.launch(ioDispatcher) {
            _lastBackupDateTime.emit(backupRepository.getLastBackUpDateTime())
        }
    }

    private fun getFileSizeAsMB(filePath: String): String = try {
        "${
            ((File(filePath)
                .length() / 1024) / 1024)
        } MB"
    } catch (t: Throwable) {
        " > 1MB"
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

