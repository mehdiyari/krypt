package ir.mehdiyari.krypt.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.file.data.entity.FileTypeEnum
import ir.mehdiyari.krypt.mediaList.MediaViewAction

@Composable
internal fun HomeRoute(
    openAddTextScreen: (String?) -> Unit,
    openTextsScreen: () -> Unit,
    openMusicAndAudioScreen: () -> Unit,
    openMediaScreen: (MediaViewAction) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    sharedDataViewModel: ir.mehdiyari.krypt.shareContent.ShareDataViewModel
) {
    viewModel.getHomeItems()
    val cards by viewModel.filesCounts.collectAsStateWithLifecycle()
    val sharedData by sharedDataViewModel.sharedData.collectAsStateWithLifecycle()
    DisposableEffect(sharedData) {
        if (sharedData != null) {
            if (sharedData is ir.mehdiyari.krypt.shareContent.SharedDataState.SharedText) {
                openAddTextScreen((sharedData as ir.mehdiyari.krypt.shareContent.SharedDataState.SharedText).text)
            } else if (sharedData is ir.mehdiyari.krypt.shareContent.SharedDataState.SharedMedias) {
                openMediaScreen(
                    MediaViewAction.SHARED_MEDIA
                )
            }
            sharedDataViewModel.clearSharedData()
        }

        onDispose { }
    }

    HomeScreen(
        cards = cards, onItemClicked = {
            when (it) {
                FileTypeEnum.Photo -> openMediaScreen(MediaViewAction.DECRYPT_MEDIA)
                FileTypeEnum.Audio -> openMusicAndAudioScreen()
                FileTypeEnum.Text -> openTextsScreen()
                else -> Unit
            }
        }, modifier = modifier
    )
}

@Composable
internal fun HomeScreen(
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
private fun HomeScreenPreview(@PreviewParameter(HomeCardsPreviewParameterProvider::class) cards: List<HomeCardsModel>) {
    KryptTheme {
        Surface {
            HomeScreen(cards = cards, onItemClicked = {})
        }
    }
}