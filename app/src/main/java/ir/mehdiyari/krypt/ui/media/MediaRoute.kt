package ir.mehdiyari.krypt.ui.media

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import ir.mehdiyari.krypt.R

@Composable
fun MediaRoute(modifier: Modifier = Modifier, viewModel: MediasViewModel = hiltViewModel()) {

    val actionState by viewModel.viewAction.collectAsStateWithLifecycle()
    val viewState by viewModel.mediaViewState.collectAsStateWithLifecycle()
    var notifyMediaScanner by remember { mutableStateOf(true) }

    MediaScreen(
        actionState = actionState,
        viewState = viewState,
        notifyMediaScanner = notifyMediaScanner,
        onNotifyChanged = { notifyMediaScanner = it },
        deleteAllSelectedFiles = { viewModel.deleteAllSelectedFiles() },
        removeItemFromList = { viewModel.removeSelectedFromList(it) },
        deleteSelectedFromList = { path, encrypted ->
            viewModel.deleteSelectedFromList(
                path,
                encrypted
            )
        },
        modifier = modifier
    )
}

@Composable
fun MediaScreen(
    actionState: MediaFragmentAction,
    viewState: MediaViewState,
    notifyMediaScanner: Boolean,
    onNotifyChanged: (Boolean) -> Unit,
    deleteAllSelectedFiles: () -> Unit,
    removeItemFromList: (String) -> Unit,
    deleteSelectedFromList: (String, Boolean) -> Unit,
    modifier: Modifier
) {

    Box(modifier = modifier) {
        when (viewState) {
            MediaViewState.Default -> CircularProgressIndicator()
            MediaViewState.OperationStart -> OperationStart()
            MediaViewState.OperationFinished -> {
                val textRes = when (actionState) {
                    MediaFragmentAction.PICK_MEDIA, MediaFragmentAction.TAKE_MEDIA, MediaFragmentAction.ENCRYPT_MEDIA -> R.string.encrypt_successfully
                    MediaFragmentAction.DECRYPT_MEDIA -> R.string.decrypt_successfully
                    else -> R.string.operation_successfully
                }
                OperationResult(imageRes = R.drawable.operation_done, messageRes = textRes)
            }

            MediaViewState.OperationFailed -> {
                val textRes = when (actionState) {
                    MediaFragmentAction.PICK_MEDIA, MediaFragmentAction.TAKE_MEDIA, MediaFragmentAction.ENCRYPT_MEDIA -> R.string.encrypt_failed
                    MediaFragmentAction.DECRYPT_MEDIA -> R.string.decrypt_failed
                    else -> R.string.operation_failed
                }
                OperationResult(imageRes = R.drawable.operation_failed, messageRes = textRes)
            }

            is MediaViewState.EncryptDecryptState -> MediaScreenContent(
                selectedMediaItems = viewState.selectedMediaItems,
                actionState = actionState,
                notifyMediaScanner = notifyMediaScanner,
                removeItemFromList = removeItemFromList,
                deleteSelectedFromList = deleteSelectedFromList,
                onNotifyChanged = onNotifyChanged,
                modifier = modifier
            )

        }

    }


    ShowActionButton(
        viewState = viewState,
        actionState = actionState,
        notifyMediaScanner = notifyMediaScanner,
        deleteAllSelectedFiles = deleteAllSelectedFiles
    )
}