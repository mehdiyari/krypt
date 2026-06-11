package ir.mehdiyari.krypt.restore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.backup.logic.restore.RestoreKeyGenerator
import ir.mehdiyari.krypt.backup.logic.restore.RestoreRepository
import ir.mehdiyari.krypt.cryptography.exceptions.DecryptException
import ir.mehdiyari.krypt.dispatchers.di.DispatchersQualifierType
import ir.mehdiyari.krypt.dispatchers.di.DispatchersType
import ir.mehdiyari.krypt.permission.checkIfAppIsStorageManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import javax.inject.Inject
import ir.mehdiyari.krypt.shared.designsystem.resources.R as DesignSystemR

@HiltViewModel
internal class RestoreViewModel @Inject constructor(
    private val restoreRepository: RestoreRepository,
    private val restoreKeyGenerator: RestoreKeyGenerator,
    @DispatchersType(DispatchersQualifierType.IO) private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _restoreViewState =
        MutableStateFlow<RestoreViewState>(RestoreViewState.OpenBackupFile)
    val restoreViewState: StateFlow<RestoreViewState> = _restoreViewState.asStateFlow()

    private val _restoreLoadingState =
        MutableStateFlow(false)
    val restoreLoadingState: StateFlow<Boolean> = _restoreLoadingState.asStateFlow()

    private var selectedFilePath: String? = null

    private val _restoreMessageSharedFlow = MutableSharedFlow<Int>()
    val restoreMessageSharedFlow = _restoreMessageSharedFlow.asSharedFlow()

    private fun restoreBackupFile(
        filePath: String,
        password: String,
    ) {
        viewModelScope.launch(ioDispatcher) {
            _restoreLoadingState.value = true
            try {
                val key = restoreKeyGenerator.generateKey(password, filePath)
                restoreRepository.restoreAll(filePath, key).getOrThrow()
                _restoreViewState.emit(RestoreViewState.Success)
            } catch (decryptException: DecryptException) {
                decryptException.printStackTrace()
                _restoreMessageSharedFlow.emit(R.string.decryption_error_restore)
            } catch (fileNotFoundException: FileNotFoundException) {
                fileNotFoundException.printStackTrace()
                _restoreMessageSharedFlow.emit(R.string.cant_find_restore_file)
            } catch (t: Throwable) {
                t.printStackTrace()
                _restoreMessageSharedFlow.emit(DesignSystemR.string.something_went_wrong)
            } finally {
                _restoreLoadingState.emit(false)
            }
        }
    }

    fun onFileSelected(realPathBasedOnUri: String?) {
        if (realPathBasedOnUri != null) {
            selectedFilePath = realPathBasedOnUri
            _restoreViewState.value = RestoreViewState.ReadyForRestoreState(
                filePath = realPathBasedOnUri,
                isExternalStoragePermissionGranted = checkIfAppIsStorageManager()
            )
        } else {
            _restoreViewState.value = RestoreViewState.Close
        }
    }

    fun onRestoreClicked(password: String) {
        restoreBackupFile(selectedFilePath!!, password)
    }

    fun checkPermissionStatus() {
        (_restoreViewState.value as? RestoreViewState.ReadyForRestoreState)?.also {
            if (it.isExternalStoragePermissionGranted != checkIfAppIsStorageManager()) {
                _restoreViewState.value = RestoreViewState.ReadyForRestoreState(
                    it.filePath, checkIfAppIsStorageManager()
                )
            }
        }
    }
}