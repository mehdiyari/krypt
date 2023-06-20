package ir.mehdiyari.krypt.ui.text.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun TextsRoute(
    onTextClick: (id: Long) -> Unit,
    onNewNoteClick: () -> Unit,
    modifier: Modifier = Modifier, viewModel: TextsViewModel = hiltViewModel()
) {

    val texts by viewModel.textFilesList.collectAsStateWithLifecycle()

    TextsScreen(
        texts = texts,
        onTextClicked = onTextClick,
        onNewNoteClicked = onNewNoteClick,
        modifier = modifier
    )

}

@Composable
fun TextsScreen(
    texts: List<TextEntity>,
    onTextClicked: (id: Long) -> Unit,
    onNewNoteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            items(texts, key = { item -> item.id }) { text ->
                TextCard(
                    text = text,
                    onTextClick = onTextClicked,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        NewNoteFab(
            newNoteClick = onNewNoteClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
        )
    }

}

@Preview(device = "id:pixel")
@Composable
fun TextsScreenPreview(@PreviewParameter(TextsPreviewParameterProvider::class) texts: List<TextEntity>) {

    KryptTheme {
        Surface {
            TextsScreen(texts = texts, onTextClicked = {}, onNewNoteClicked = {})
        }
    }

}

class TextsPreviewParameterProvider : PreviewParameterProvider<List<TextEntity>> {
    override val values: Sequence<List<TextEntity>>
        get() = sequenceOf(List(5) {
            TextEntity(id = it.toLong(), title = "Title $it", content = "Content $it")
        })

}