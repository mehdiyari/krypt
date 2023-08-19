package ir.mehdiyari.krypt.restore

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.restore.view.OpenBackupFile

@Composable
internal fun RestoreRoute(
    viewModel: RestoreViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val viewState = viewModel.restoreViewState.collectAsStateWithLifecycle()

    when (viewState.value) {
        RestoreViewState.OpenBackupFile -> OpenBackupFile(viewModel::onFileSelected)
        RestoreViewState.ReadyForRestoreState -> TODO()
    }

}