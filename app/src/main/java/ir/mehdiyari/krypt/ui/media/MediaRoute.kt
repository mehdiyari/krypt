package ir.mehdiyari.krypt.ui.media

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.fallery.main.fallery.FalleryOptions
import ir.mehdiyari.fallery.main.fallery.getFalleryActivityResultContract
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.ManageExternalPermissionDialog
import ir.mehdiyari.krypt.utils.checkIfAppIsStorageManager
import ir.mehdiyari.krypt.utils.requestGrantManagerStoragePermission

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MediaRoute(
    modifier: Modifier = Modifier,
    viewModel: MediasViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onStopLocker: () -> Unit,
) {
    val managerStoragePermissionState = remember { mutableStateOf(false) }
    val actionState by viewModel.viewAction.collectAsStateWithLifecycle()
    val viewState by viewModel.mediaViewState.collectAsStateWithLifecycle()
    var notifyMediaScanner by remember { mutableStateOf(true) }

    if (actionState == MediaViewAction.DEFAULT) {
        onBackPressed()
        return
    }

    MediaScreen(modifier = modifier,
        actionState = actionState,
        viewState = (viewState as? MediaViewState.EncryptDecryptState)?.let {
            it.copy(onEncryptOrDecryptAction = { deleteAfterEncryption, notifyMediaScanner ->
                if (!checkIfAppIsStorageManager()) {
                    managerStoragePermissionState.value = true
                } else {
                    it.onEncryptOrDecryptAction.invoke(deleteAfterEncryption, notifyMediaScanner)
                }
            })
        } ?: viewState,
        notifyMediaScanner = notifyMediaScanner,
        onNotifyChanged = { notifyMediaScanner = it },
        deleteAllSelectedFiles = {
            if (!checkIfAppIsStorageManager()) {
                managerStoragePermissionState.value = true
            } else {
                viewModel.deleteAllSelectedFiles()
            }
        },
        removeItemFromList = { viewModel.removeSelectedFromList(it) },
        deleteSelectedFromList = { path, encrypted ->
            if (!checkIfAppIsStorageManager()) {
                managerStoragePermissionState.value = true
            } else {
                viewModel.deleteSelectedFromList(
                    path, encrypted
                )
            }
        },
        defaultFalleryOptions = viewModel.getDefaultFalleryOptions(),
        kryptFalleryOptions = viewModel.getKryptFalleryOptions(),
        onBackPressed = onBackPressed,
        onSelectMedia = viewModel::onSelectedMedias,
        checkForOpenPickerForDecryptMode = viewModel::checkForOpenPickerForDecryptMode,
        onStopAutoLocker = onStopLocker
    )

    val messageState by viewModel.messageFlow.collectAsStateWithLifecycle(null)
    if (messageState != null) {
        Toast.makeText(
            LocalContext.current, messageState ?: R.string.something_went_wrong, Toast.LENGTH_SHORT
        ).show()
    }

    val context = LocalContext.current
    if (managerStoragePermissionState.value) {
        ManageExternalPermissionDialog(modifier = Modifier, state = managerStoragePermissionState) {
            managerStoragePermissionState.value = false
            context.requestGrantManagerStoragePermission()
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MediaScreen(
    modifier: Modifier,
    actionState: MediaViewAction,
    viewState: MediaViewState,
    notifyMediaScanner: Boolean,
    onNotifyChanged: (Boolean) -> Unit,
    deleteAllSelectedFiles: () -> Unit,
    removeItemFromList: (String) -> Unit,
    deleteSelectedFromList: (String, Boolean) -> Unit,
    defaultFalleryOptions: FalleryOptions,
    kryptFalleryOptions: FalleryOptions,
    onBackPressed: () -> Unit,
    onSelectMedia: (List<String>) -> Unit,
    checkForOpenPickerForDecryptMode: () -> Boolean,
    onStopAutoLocker: () -> Unit,
) {
    val isFalleryOpenedBefore = remember { mutableStateOf(false) }
    val falleryLauncher = rememberLauncherForActivityResult(getFalleryActivityResultContract()) {
        if (actionState == MediaViewAction.DECRYPT_MEDIA && it.mediaPathList.isNullOrEmpty()) {
            onBackPressed()
            return@rememberLauncherForActivityResult
        }
        onSelectMedia(it.mediaPathList ?: listOf())
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        when (viewState) {
            MediaViewState.Default -> {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            MediaViewState.OperationStart -> OperationStart()
            MediaViewState.OperationFinished -> {
                val textRes = when (actionState) {
                    MediaViewAction.PICK_MEDIA, MediaViewAction.TAKE_MEDIA, MediaViewAction.ENCRYPT_MEDIA -> R.string.encrypt_successfully
                    MediaViewAction.DECRYPT_MEDIA -> R.string.decrypt_successfully
                    else -> R.string.operation_successfully
                }
                OperationResult(imageRes = R.drawable.operation_done, messageRes = textRes)
            }

            MediaViewState.OperationFailed -> {
                val textRes = when (actionState) {
                    MediaViewAction.PICK_MEDIA, MediaViewAction.TAKE_MEDIA, MediaViewAction.ENCRYPT_MEDIA -> R.string.encrypt_failed
                    MediaViewAction.DECRYPT_MEDIA -> R.string.decrypt_failed
                    else -> R.string.operation_failed
                }
                OperationResult(imageRes = R.drawable.operation_failed, messageRes = textRes)
            }

            is MediaViewState.EncryptDecryptState -> {
                if (viewState.selectedMediaItems.isEmpty()) {
                    onBackPressed()
                    return
                } else {
                    MediaScreenContent(
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

        }

        ShowActionButton(
            viewState = viewState,
            actionState = actionState,
            notifyMediaScanner = notifyMediaScanner,
            deleteAllSelectedFiles = deleteAllSelectedFiles,
            modifier = modifier,
        )
    }


    if (viewState == MediaViewState.Default) {
        if (!isFalleryOpenedBefore.value) {
            if (actionState == MediaViewAction.PICK_MEDIA) {
                SideEffect {
                    onStopAutoLocker()
                    falleryLauncher.launch(defaultFalleryOptions)
                    isFalleryOpenedBefore.value = true
                }
            } else if (actionState == MediaViewAction.DECRYPT_MEDIA) {
                val context = LocalContext.current
                if (!checkForOpenPickerForDecryptMode()) {
                    Toast.makeText(
                        context, R.string.no_encrypted_file_found, Toast.LENGTH_LONG
                    ).show()
                    onBackPressed()
                } else {
                    SideEffect {
                        onStopAutoLocker()
                        falleryLauncher.launch(kryptFalleryOptions)
                        isFalleryOpenedBefore.value = true
                    }
                }
            }
        }
    }
}