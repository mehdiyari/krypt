package ir.mehdiyari.krypt.ui.data

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun DataRoute(
    viewModel: DataViewModel = hiltViewModel(),
    onNavigationClicked: () -> Unit,
    modifier: Modifier,
) {
    DataScreenScaffold(modifier = modifier, onNavigationClicked = onNavigationClicked) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            val fileSizeState = viewModel.fileSizes.collectAsStateWithLifecycle()
            FileSizeView(modifier, fileSizeState)
            val lastBackupState = viewModel.lastBackupDateTime.collectAsStateWithLifecycle()
            val backupState = viewModel.backupViewState.collectAsStateWithLifecycle()
            BackupView(modifier, lastBackupState, backupState, viewModel::backupNow)
            val backupList = viewModel.backups.collectAsStateWithLifecycle()

            val deleteDialogState = remember { mutableStateOf(false to -1) }
            BackupList(modifier, backupList, viewModel::onSaveBackup) {
                deleteDialogState.value = true to it
            }

            DeleteBackupFileDialog(modifier, deleteDialogState, viewModel::onDeleteBackup)
        }
    }

    val message = viewModel.generalMessageFlow.collectAsStateWithLifecycle(initialValue = null)
    if (message.value != null) {
        if (message.value == R.string.saving_backup_permission_error) {
            // TODO: request storage permission
        } else {
            Toast.makeText(
                LocalContext.current, message.value!!, Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun DataRoutePreview(
    @PreviewParameter(
        BackupsPreviewParameterProvider::class,
        limit = 5
    ) backupList: List<BackupViewData>
) {
    KryptTheme {
        DataScreenScaffold(modifier = Modifier) {
            Column {
                FileSizePreview()
                BackupViewPreview()
                BackupListPreview(backupList = backupList)
            }
        }
    }
}