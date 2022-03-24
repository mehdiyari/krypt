package ir.mehdiyari.krypt.ui.photo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.photo.PhotosFragmentAction.*
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
@Preview
fun PhotosComposeScreen(
    viewModel: PhotosViewModel = viewModel(),
    onNavigationClickIcon: () -> Unit = {}
) {
    KryptTheme {
        val actionState = viewModel.viewAction.collectAsState()
        val viewState = viewModel.photosViewState.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.photos_library))
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    if (viewState.value is PhotosViewState.Default) {
                        BaseViewLoading()
                    } else {
                        when (viewState.value) {
                            is PhotosViewState.OperationStart -> {
                                OperationStartView()
                            }
                            is PhotosViewState.OperationFinished -> {
                                OperationFinishedView(actionState.value)
                            }
                            is PhotosViewState.OperationFailed -> {
                                OperationFailedView(actionState.value)
                            }
                            is PhotosViewState.EncryptDecryptState -> {
                                val encryptDecryptState =
                                    viewState.value as PhotosViewState.EncryptDecryptState
                                if (actionState.value == PICK_PHOTO || actionState.value == PICK_PHOTO) {
                                    EncryptSelectedPhotosView(encryptDecryptState)
                                } else if (actionState.value == DECRYPT_PHOTO) {
                                    DecryptSelectedPhotosView(encryptDecryptState)
                                }
                            }
                        }
                    }
                }
            })
    }
}

@Composable
fun OperationFailedView(value: PhotosFragmentAction) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val text = when (value) {
            PICK_PHOTO, TAKE_PHOTO -> {
                stringResource(id = R.string.encrypt_failed)
            }
            DECRYPT_PHOTO -> {
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
fun OperationFinishedView(value: PhotosFragmentAction) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val text = when (value) {
            PICK_PHOTO, TAKE_PHOTO -> {
                stringResource(id = R.string.encrypt_successfully)
            }
            DECRYPT_PHOTO -> {
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
fun BaseEncryptDecryptView(
    mainText: String,
    buttonText: String,
    deleteFileDialogTestResId: Int,
    onButtonClick: (delete: Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = mainText,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 15.dp),
            textAlign = TextAlign.Justify,
            fontSize = 18.sp
        )

        val context = LocalContext.current
        Button(
            modifier = Modifier
                .size(100.dp),
            shape = CircleShape,
            onClick = {
                androidx.appcompat.app.AlertDialog.Builder(context)
                    .setMessage(context.getString(deleteFileDialogTestResId))
                    .setPositiveButton(context.getString(R.string.YES)) { d, _ ->
                        d.dismiss()
                        onButtonClick(true)
                    }
                    .setNegativeButton(context.getString(R.string.NO)) { d, _ ->
                        d.dismiss()
                        onButtonClick(false)
                    }.create().show()
            }) {
            Text(
                text = buttonText
            )
        }
    }
}

@Composable
fun EncryptSelectedPhotosView(
    encryptDecryptState: PhotosViewState.EncryptDecryptState
) {
    BaseEncryptDecryptView(
        mainText = stringResource(
            id = R.string.x_photos_selected_for_encryption,
            encryptDecryptState.selectedPhotosCount
        ),
        buttonText = stringResource(id = R.string.encrypt_action),
        deleteFileDialogTestResId = R.string.delete_photos_after_encrypt_dialog_message,
        onButtonClick = encryptDecryptState.onEncryptOrDecryptAction
    )
}

@Composable
fun DecryptSelectedPhotosView(
    encryptDecryptState: PhotosViewState.EncryptDecryptState
) {
    BaseEncryptDecryptView(
        mainText = stringResource(
            id = R.string.x_photos_selected_for_decryption,
            encryptDecryptState.selectedPhotosCount
        ),
        buttonText = stringResource(id = R.string.decrypt_action),
        deleteFileDialogTestResId = R.string.delete_photos_after_decrypt_dialog_message,
        onButtonClick = encryptDecryptState.onEncryptOrDecryptAction
    )
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
