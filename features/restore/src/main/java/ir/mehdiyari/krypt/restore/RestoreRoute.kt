package ir.mehdiyari.krypt.restore

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.permission.requestGrantManagerStoragePermission
import ir.mehdiyari.krypt.restore.view.OpenBackupFile
import ir.mehdiyari.krypt.restore.view.ReadyForRestore

@Composable
internal fun RestoreRoute(
    modifier: Modifier,
    viewModel: RestoreViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val viewState = viewModel.restoreViewState.collectAsStateWithLifecycle()
    val restoreLoadingState = viewModel.restoreLoadingState.collectAsStateWithLifecycle()
    val lifeCycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    when (viewState.value) {
        RestoreViewState.Close -> onBackPressed()
        RestoreViewState.OpenBackupFile -> OpenBackupFile(viewModel::onFileSelected)
        is RestoreViewState.ReadyForRestoreState -> (viewState.value as RestoreViewState.ReadyForRestoreState).also {
            ReadyForRestore(
                modifier = modifier,
                filePath = it.filePath,
                isPermissionGranted = it.isExternalStoragePermissionGranted,
                onRequestPermission = {
                    context.requestGrantManagerStoragePermission()
                },
                onRestoreClicked = viewModel::onRestoreClicked,
                onBackPressed = onBackPressed,
                loadingState = restoreLoadingState.value,
            )
        }

        is RestoreViewState.Success -> {
            TODO()
        }
    }

    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissionStatus()
            }
        }

        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose { lifeCycleOwner.lifecycle.removeObserver(observer) }
    }

    val restoreMessageState = viewModel.restoreMessageSharedFlow.collectAsStateWithLifecycle(
        initialValue = null
    )
    if (restoreMessageState.value != null) {
        Toast.makeText(LocalContext.current, restoreMessageState.value!!, Toast.LENGTH_SHORT).show()
    }
}