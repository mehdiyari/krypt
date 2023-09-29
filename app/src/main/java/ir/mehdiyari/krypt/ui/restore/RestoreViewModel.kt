package ir.mehdiyari.krypt.ui.restore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.mehdiyari.krypt.backup.logic.restore.RestoreRepository
import ir.mehdiyari.krypt.cryptography.exceptions.DecryptException
import kotlinx.coroutines.launch
import okio.FileNotFoundException

class RestoreViewModel(
    private val restoreRepository: RestoreRepository,
    private val restoreKeyGenerator: RestoreKeyGenerator,
) : ViewModel() {

    fun restoreBackupFile(
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

}