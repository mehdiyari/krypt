package ir.mehdiyari.krypt.restore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.backup.logic.restore.RestoreKeyGenerator
import ir.mehdiyari.krypt.backup.logic.restore.RestoreRepository
import ir.mehdiyari.krypt.cryptography.exceptions.DecryptException
import ir.mehdiyari.krypt.permission.checkIfAppIsStorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import javax.inject.Inject

@HiltViewModel
internal class RestoreViewModel @Inject constructor(
    private val restoreRepository: RestoreRepository,
    private val restoreKeyGenerator: RestoreKeyGenerator,
) : ViewModel() {

    private val _restoreViewState =
        MutableStateFlow<RestoreViewState>(RestoreViewState.OpenBackupFile)
    val restoreViewState: StateFlow<RestoreViewState> = _restoreViewState.asStateFlow()
    private var selectedFilePath: String? = null

    private fun restoreBackupFile(
        filePath: String,
        password: String,
    ) {
        viewModelScope.launch {
            try {
                val key = restoreKeyGenerator.generateKey(password, filePath)
                restoreRepository.restoreAll(filePath, key).getOrThrow()
                TODO("Success")
            } catch (decryptException: DecryptException) {
                TODO("Handle the error")
            } catch (fileNotFoundException: FileNotFoundException) {
                TODO("Handle the error")
            } catch (t: Throwable) {
                TODO("Handle the error")
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

    fun onRestoreClicked() {
        TODO("Not yet implemented")
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