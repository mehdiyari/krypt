package ir.mehdiyari.krypt.ui.media.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.crypto.KryptCryptographyHelper
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.utils.FilesUtilities
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val kryptCryptographyHelper: KryptCryptographyHelper,
    private val fileUtils: FilesUtilities,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _playerViewState = MutableStateFlow<PlayerState?>(null)
    fun getPlayerViewStateState() = _playerViewState.asStateFlow()

    fun onEncryptedVideoReceived(
        path: String
    ) {
        viewModelScope.launch(ioDispatcher) {
            if (getPlayerViewStateState().value == null) {
                val newPath = fileUtils.generateCacheVideoPath(File(path).name)
                if (File(newPath).exists()) {
                    _playerViewState.emit(PlayerState.EncryptedCashedVideo(newPath))
                } else {
                    try {
                        _playerViewState.emit(PlayerState.Decrypting)
                        if (kryptCryptographyHelper.decryptFile(path, newPath).isSuccess) {
                            _playerViewState.emit(PlayerState.EncryptedCashedVideo(newPath))
                        } else {
                            _playerViewState.emit(PlayerState.ForceClose)
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        _playerViewState.emit(PlayerState.ForceClose)
                    }
                }
            }
        }
    }

    fun normalVideo(path: String) {
        viewModelScope.launch {
            if (getPlayerViewStateState().value == null) {
                _playerViewState.emit(PlayerState.NormalVideo(path))
            }
        }
    }

    override fun onCleared() {
        fileUtils.deleteCachedVideoDIR()
        super.onCleared()
    }
}