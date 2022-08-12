package ir.mehdiyari.krypt.ui.media

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.media.MediaFragmentAction.*
import ir.mehdiyari.krypt.utils.KryptTheme

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MediasComposeScreen(
    viewModel: MediasViewModel = viewModel(),
    onNavigationClickIcon: () -> Unit = {}
) {
    KryptTheme {
        val actionState = viewModel.viewAction.collectAsState()
        val viewState = viewModel.mediaViewState.collectAsState()
        val notifyMediaScanner = remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.medias_library), fontSize = 18.sp)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onNavigationClickIcon()
                        }) {
                            Icon(Icons.Filled.ArrowBack, "")
                        }
                    }
                )
            }, content = {
                MediaScreenContent(viewState, actionState, notifyMediaScanner, viewModel)
            })


        ShowActionButton(viewState, actionState, notifyMediaScanner, viewModel)
    }
}

@Composable
private fun MediaScreenContent(
    viewState: State<MediaViewState>,
    actionState: State<MediaFragmentAction>,
    notifyMediaScanner: MutableState<Boolean>,
    viewModel: MediasViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        if (viewState.value is MediaViewState.Default) {
            BaseViewLoading()
        } else {
            val deleteFileDialogState =
                remember<MutableState<Pair<Boolean, (() -> Unit)?>>> { mutableStateOf(false to {}) }
            when (viewState.value) {
                is MediaViewState.OperationStart -> {
                    OperationStartView()
                }
                is MediaViewState.OperationFinished -> {
                    OperationFinishedView(actionState.value)
                }
                is MediaViewState.OperationFailed -> {
                    OperationFailedView(actionState.value)
                }
                is MediaViewState.EncryptDecryptState -> {
                    val encryptDecryptState =
                        viewState.value as MediaViewState.EncryptDecryptState
                    if (actionState.value == PICK_MEDIA || actionState.value == ENCRYPT_MEDIA) {
                        EncryptSelectedMediaView(
                            encryptDecryptState,
                            onRemoveClicked = {
                                viewModel.removeSelectedFromList(it)
                            },
                            onDeleteClicked = {
                                deleteFileDialogState.value = true to {
                                    viewModel.deleteSelectedFromList(it, isEncrypted = false)
                                }
                            }
                        )
                    } else if (actionState.value == DECRYPT_MEDIA) {
                        DecryptSelectedMediaView(
                            encryptDecryptState,
                            notifyMediaScanner,
                            onRemoveClicked = {
                                viewModel.removeSelectedFromList(it)
                            },
                            onDeleteClicked = {
                                deleteFileDialogState.value = true to {
                                    viewModel.deleteSelectedFromList(it, isEncrypted = true)
                                }
                            }
                        )
                    }
                }
            }

            if (deleteFileDialogState.value.first) {
                ShowDeleteFileDialog(
                    deleteFileDialogState,
                    stringResource(id = R.string.delete_selected_file)
                )
            }
        }
    }
}

@Composable
fun ShowDeleteFileDialog(
    deleteFileDialogState: MutableState<Pair<Boolean, (() -> Unit)?>>,
    description: String
) {
    AlertDialog(
        onDismissRequest = {
            deleteFileDialogState.value = false to null
        },
        title = {
            Text(text = stringResource(id = R.string.delete_file))
        },
        text = {
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = description
            )
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    deleteFileDialogState.value.second?.invoke()
                    deleteFileDialogState.value = false to null
                },
            ) {
                Text(stringResource(id = R.string.YES))
            }

        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    deleteFileDialogState.value = false to null
                }
            ) {
                Text(stringResource(id = R.string.NO))
            }
        }
    )
}

@Composable
private fun ShowActionButton(
    viewState: State<MediaViewState>,
    actionState: State<MediaFragmentAction>,
    notifyMediaScanner: MutableState<Boolean>,
    viewModel: MediasViewModel
) {
    val context = LocalContext.current
    if (viewState.value is MediaViewState.EncryptDecryptState) {

        val buttonTextAndDeleteText: Pair<String, String> =
            when (actionState.value) {
                PICK_MEDIA, ENCRYPT_MEDIA -> {
                    (stringResource(id = R.string.encrypt_action) to stringResource(id = R.string.delete_files_after_encrypt_dialog_message))
                }
                DECRYPT_MEDIA -> {
                    stringResource(id = R.string.decrypt_action) to stringResource(id = R.string.delete_files_after_decrypt_dialog_message)
                }
                else -> {
                    "" to ""
                }
            }

        val encryptDecryptState = viewState.value as? MediaViewState.EncryptDecryptState

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(20.dp)
        ) {
            ActionFloatingButton(buttonTextAndDeleteText.first) {
                androidx.appcompat.app.AlertDialog.Builder(context)
                    .setMessage(buttonTextAndDeleteText.second)
                    .setPositiveButton(context.getString(R.string.YES)) { d, _ ->
                        d.dismiss()
                        encryptDecryptState?.onEncryptOrDecryptAction?.invoke(
                            true,
                            notifyMediaScanner.value
                        )
                    }
                    .setNegativeButton(context.getString(R.string.NO)) { d, _ ->
                        d.dismiss()
                        encryptDecryptState?.onEncryptOrDecryptAction?.invoke(
                            false,
                            notifyMediaScanner.value
                        )
                    }.create().show()
            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(20.dp)
        ) {
            DeleteAllFilesFloatingButton(viewModel = viewModel)
        }
    }
}

@Composable
fun OperationFailedView(value: MediaFragmentAction) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val text = when (value) {
            PICK_MEDIA, TAKE_MEDIA, ENCRYPT_MEDIA -> {
                stringResource(id = R.string.encrypt_failed)
            }
            DECRYPT_MEDIA -> {
                stringResource(id = R.string.decrypt_failed)
            }
            else -> {
                stringResource(id = R.string.operation_failed)
            }
        }

        Image(
            painter = painterResource(R.drawable.operation_failed),
            contentDescription = text,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OperationFinishedView(value: MediaFragmentAction) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val text = when (value) {
            PICK_MEDIA, TAKE_MEDIA, ENCRYPT_MEDIA -> {
                stringResource(id = R.string.encrypt_successfully)
            }
            DECRYPT_MEDIA -> {
                stringResource(id = R.string.decrypt_successfully)
            }
            else -> {
                stringResource(id = R.string.operation_successfully)
            }
        }
        Image(
            painter = painterResource(R.drawable.operation_done),
            contentDescription = text,
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OperationStartView() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(id = R.string.loading),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EncryptSelectedMediaView(
    encryptDecryptState: MediaViewState.EncryptDecryptState,
    onRemoveClicked: (String) -> Unit = {},
    onDeleteClicked: (String) -> Unit = {}
) {
    BaseEncryptDecryptView(
        encryptDecryptState = encryptDecryptState,
        onRemoveClicked = onRemoveClicked,
        onDeleteClicked = onDeleteClicked
    )
}

@Composable
fun DecryptSelectedMediaView(
    encryptDecryptState: MediaViewState.EncryptDecryptState,
    notifyMediaScanner: MutableState<Boolean>,
    onRemoveClicked: (String) -> Unit = {},
    onDeleteClicked: (String) -> Unit = {}
) {
    NotifyMediaScannerCheckBox(notifyMediaScanner)
    BaseEncryptDecryptView(
        encryptDecryptState = encryptDecryptState,
        onRemoveClicked = onRemoveClicked,
        onDeleteClicked = onDeleteClicked
    )
}

@Composable
private fun NotifyMediaScannerCheckBox(notifyMediaScanner: MutableState<Boolean>) {
    Row(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 0.dp)) {
        Checkbox(checked = notifyMediaScanner.value, onCheckedChange = {
            notifyMediaScanner.value = it
        })

        Text(
            text = stringResource(id = R.string.notify_media_scanner),
            textAlign = TextAlign.Start,
            modifier = Modifier
                .selectable(false, onClick = {
                    notifyMediaScanner.value = !notifyMediaScanner.value
                })
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
@Preview
fun BaseEncryptDecryptView(
    encryptDecryptState: MediaViewState.EncryptDecryptState = MediaViewState.EncryptDecryptState(
        listOf(
            SelectedMediaItems("", true),
            SelectedMediaItems("", true),
            SelectedMediaItems("", true)
        )
    ) { _, _ -> },
    content: @Composable () -> Unit = {},
    onRemoveClicked: (String) -> Unit = {},
    onDeleteClicked: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        FileList(
            encryptDecryptState.selectedMediaItems,
            onRemoveClicked,
            onDeleteClicked
        )
        content()
    }
}

@Composable
@Preview
fun ActionFloatingButton(
    buttonText: String = stringResource(id = R.string.encrypt_action),
    onButtonClick: () -> Unit = {}
) {
    ExtendedFloatingActionButton(
        onClick = {
            onButtonClick()
        },
        text = { Text(text = buttonText) }
    )
}


@Composable
fun DeleteAllFilesFloatingButton(
    onButtonClick: () -> Unit = {},
    viewModel: MediasViewModel
) {
    val deleteAllFileDialogState =
        remember<MutableState<Pair<Boolean, (() -> Unit)?>>> { mutableStateOf(false to {}) }
    ExtendedFloatingActionButton(
        onClick = {
            deleteAllFileDialogState.value = true to {
                viewModel.deleteAllSelectedFiles()
            }
            onButtonClick()
        },
        text = { Text(text = stringResource(id = R.string.delete_all)) },
        backgroundColor = MaterialTheme.colors.error,
        contentColor = Color.White
    )

    if (deleteAllFileDialogState.value.first) {
        ShowDeleteFileDialog(
            deleteAllFileDialogState,
            stringResource(id = R.string.delete_all_selected_files)
        )
    }
}

@Composable
fun FileList(
    selectedMediaItems: List<SelectedMediaItems>,
    onRemoveClicked: (String) -> Unit = {},
    onDeleteClicked: (String) -> Unit = {}
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = 10.dp,
            bottom = 80.dp
        )
    ) {
        items(selectedMediaItems) { selectedMediaItem ->
            FileItem(selectedMediaItem, onRemoveClicked, onDeleteClicked)
        }
    }
}

@Composable
@Preview
fun FileItem(
    item: SelectedMediaItems = SelectedMediaItems("", true),
    onRemoveClicked: (String) -> Unit = {},
    onDeleteClicked: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            GlideImage(
                imageModel = item.path,
                requestOptions = {
                    RequestOptions()
                        .override(80, 80)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                },
                modifier = Modifier
                    .padding(top = 8.dp, start = 8.dp, bottom = 8.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(modifier = Modifier.padding(top = 10.dp, start = 8.dp, end = 4.dp)) {
                Text(
                    text = item.getFileRealName(),
                    fontSize = 16.sp,
                    maxLines = 2
                )

                Text(
                    text = item.getFileSize(),
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.size(2.dp))
        }


        Column(
            horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(8.dp)
        ) {
            Row {

                Icon(
                    Icons.Filled.Delete, "", modifier = Modifier
                        .selectable(false, onClick = {
                            onDeleteClicked(item.path)
                        }, role = Role.Button, enabled = true)
                        .padding(start = 4.dp, end = 4.dp)
                        .size(24.dp)
                )

                Icon(
                    painter = painterResource(R.drawable.ic_remove), "", modifier = Modifier
                        .selectable(false, onClick = {
                            onRemoveClicked(item.path)
                        }, role = Role.Button, enabled = true)
                        .padding(start = 4.dp, end = 4.dp)
                        .size(24.dp)
                )
            }
        }

    }
}

@Composable
fun BaseViewLoading() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        CircularProgressIndicator()
    }
}
