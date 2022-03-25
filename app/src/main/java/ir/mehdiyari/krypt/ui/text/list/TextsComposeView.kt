package ir.mehdiyari.krypt.ui.text.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
@Preview
fun TextsComposeView(
    viewModel: TextsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigationClickIcon: () -> Unit = {},
    newNoteClick: () -> Unit = {},
    onCardsClick: (id: Long) -> Unit = { _ -> }
) {
    KryptTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.texts_library))
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
                val textLists = viewModel.textFilesList.collectAsState()
                Row {
                    TextsLazyList(textLists, onCardsClick)
                }
            })

        NewNoteFab(newNoteClick)
    }
}

@Composable
private fun NewNoteFab(
    newNoteClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(15.dp)
    ) {
        ExtendedFloatingActionButton(
            onClick = {
                newNoteClick()
            },
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_pen),
                    contentDescription = stringResource(id = R.string.add_new_text)
                )
            },
            text = { Text(text = stringResource(id = R.string.add_new_text)) },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun TextsLazyList(
    textLists: State<List<TextEntity>>,
    onCardsClick: (id: Long) -> Unit = { _ -> }
) {
    LazyColumn(
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        contentPadding = PaddingValues(bottom = 85.dp),
    ) {
        items(textLists.value) { textEntity ->
            TextCard(textEntity, onCardsClick)
        }
    }
}

@Composable
@Preview
fun TextCard(
    textEntity: TextEntity = TextEntity(1, "Hello World", "This is Test Content...."),
    onCardsClick: (id: Long) -> Unit = { _ -> }
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 6.dp, 8.dp, 6.dp)
            .selectable(
                selected = false,
                onClick = { onCardsClick.invoke(textEntity.id) }),
        shape = RoundedCornerShape(8.dp),
        elevation = 5.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_pen),
                modifier = Modifier
                    .size(45.dp)
                    .padding(10.dp, 10.dp, 5.dp, 0.dp),
                contentDescription = "",
                colorFilter = ColorFilter.tint(Color.Gray)
            )

            Column(
                modifier = Modifier
                    .padding(4.dp, 10.dp, 8.dp, 10.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = textEntity.title,
                    maxLines = 1
                )
                Text(
                    text = textEntity.content,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}
