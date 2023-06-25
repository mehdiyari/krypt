package ir.mehdiyari.krypt.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.ui.media.MediaViewAction
import ir.mehdiyari.krypt.ui.media.SharedMediaListModel
import ir.mehdiyari.krypt.utils.KryptTheme
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    openTextsScreen: (String?) -> Unit,
    openMusicAndAudioScreen: () -> Unit,
    openMediaScreen: (MediaViewAction, SharedMediaListModel?) -> Unit,
    openAudioRecorderScreen: () -> Unit,
    onShowSnackbar: suspend (String, String) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    shareDataViewModel: ShareDataViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // FIXME MHD: Handle Shared view model
) {
    viewModel.getHomeItems()
    val cards by viewModel.filesCounts.collectAsStateWithLifecycle()
    val sharedData by shareDataViewModel.sharedData.collectAsStateWithLifecycle()

    DisposableEffect(sharedData) {
        if (sharedData != null) {
            if (sharedData is SharedDataState.SharedText) {
                openTextsScreen((sharedData as SharedDataState.SharedText).text)
            } else if (sharedData is SharedDataState.SharedMedias) {
                openMediaScreen(
                    MediaViewAction.ENCRYPT_MEDIA,
                    SharedMediaListModel((sharedData as SharedDataState.SharedMedias).medias)
                )
            }
            shareDataViewModel.clearSharedData()
        }

        onDispose { }
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val requestMICPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openAudioRecorderScreen()
        } else {
            scope.launch {
                val snackbarActionPreformed = onShowSnackbar(
                    context.getString(R.string.mic_permission_denied),
                    context.getString(R.string.grant)
                )

                if (snackbarActionPreformed) {
                    // FIXME MHD: get permission again
                }
            }
        }
    }

    HomeScreen(
        cards = cards, onItemClicked = {
            when (it) {
                FileTypeEnum.Photo -> openMediaScreen(MediaViewAction.DECRYPT_MEDIA, null)
                FileTypeEnum.Audio -> openMusicAndAudioScreen()
                FileTypeEnum.Text -> openTextsScreen(null)
                else -> Unit
            }
        }, modifier = modifier
    )
}

@Composable
fun HomeScreen(
    cards: List<HomeCardsModel>,
    onItemClicked: (FileTypeEnum) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(cards) { item: HomeCardsModel ->
            HomeItemCard(item, {
                onItemClicked(it)
            })
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview(@PreviewParameter(HomeCardsPreviewParameterProvider::class) cards: List<HomeCardsModel>) {
    KryptTheme {
        Surface {
            HomeScreen(cards = cards, onItemClicked = {})
        }
    }
}