package ir.mehdiyari.krypt.backup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.permission.checkIfAppIsStorageManager
import ir.mehdiyari.krypt.permission.requestGrantManagerStoragePermission
import ir.mehdiyari.krypt.shared.designsystem.components.ManageExternalPermissionDialog

@Composable
internal fun DataRoute(
    viewModel: DataViewModel = hiltViewModel(),
    onNavigationClicked: () -> Unit,
    modifier: Modifier,
) {
    val managerStoragePermissionState = remember { mutableStateOf(false) }

    val chooseDirectorySnackbarMsg =
        stringResource(id = R.string.choose_backup_directory_description)
    val context = LocalContext.current

    var selectedDirectory: DocumentFile? by remember { mutableStateOf(null) }
    val directoryChooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    selectedDirectory = DocumentFile.fromTreeUri(
                        context, it
                    )
                }
            }
        }
    )

    selectedDirectory?.let {
        viewModel.backupNow(it.uri)
    }

    DataScreenScaffold(
        modifier = modifier,
        onNavigationClicked = onNavigationClicked
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            val fileSizeState = viewModel.fileSizes.collectAsStateWithLifecycle()
            FileSizeView(modifier, fileSizeState)
            val lastBackupState = viewModel.lastBackupDateTime.collectAsStateWithLifecycle()
            val backupState = viewModel.backupViewState.collectAsStateWithLifecycle()
            BackupView(modifier, lastBackupState, backupState, backupNowClick = {
                if (!checkIfAppIsStorageManager()) {
                    managerStoragePermissionState.value = true
                } else {
                    Toast.makeText(
                        context, chooseDirectorySnackbarMsg, Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    directoryChooserLauncher.launch(intent)
                }
            })

            val backupList = viewModel.backups.collectAsStateWithLifecycle()

            val deleteDialogState = remember { mutableStateOf(false to -1) }
            BackupList(modifier, backupList, {
                if (!checkIfAppIsStorageManager()) {
                    managerStoragePermissionState.value = true
                } else {
                    viewModel.onSaveBackup(it)
                }
            }) {
                deleteDialogState.value = true to it
            }

            DeleteBackupFileDialog(modifier, deleteDialogState, viewModel::onDeleteBackup)
        }
    }

    val message = viewModel.generalMessageFlow.collectAsStateWithLifecycle(initialValue = null)
    if (message.value != null) {
        if (message.value == R.string.saving_backup_permission_error) {
            managerStoragePermissionState.value = true
        } else {
            Toast.makeText(
                LocalContext.current, message.value!!, Toast.LENGTH_SHORT
            ).show()
        }
    }

    if (managerStoragePermissionState.value) {
        ManageExternalPermissionDialog(modifier = modifier, state = managerStoragePermissionState) {
            managerStoragePermissionState.value = false
            context.requestGrantManagerStoragePermission()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
internal fun DataRoutePreview(
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