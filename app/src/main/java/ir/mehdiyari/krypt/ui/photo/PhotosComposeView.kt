package ir.mehdiyari.krypt.ui.photo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.photo.PhotosFragmentAction.DECRYPT_PHOTO
import ir.mehdiyari.krypt.ui.photo.PhotosFragmentAction.PICK_PHOTO
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
@Preview
fun PhotosComposeScreen(
    viewModel: PhotosViewModel = viewModel()
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
                        IconButton(onClick = { }) {
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
                        if (viewState.value is PhotosViewState.EncryptDecryptState) {
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
            })
    }
}

@Composable
fun BaseEncryptDecryptView(
    mainText: String,
    buttonText: String,
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

        Button(
            modifier = Modifier
                .size(100.dp),
            shape = CircleShape,
            onClick = {
                onButtonClick(true)
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
        onButtonClick = encryptDecryptState.onEncryptOrDecryptAction
    )
}

@Composable
fun BaseViewLoading() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}
