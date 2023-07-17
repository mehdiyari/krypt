package ir.mehdiyari.krypt.ui.text.add

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onNavigationClickIcon: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp),
    ) {
        Row {
            IconButton(modifier = Modifier
                .fillMaxHeight()
                .padding(0.dp), onClick = {
                onNavigationClickIcon()
            }) {
                Icon(Icons.Filled.ArrowBack, "")
            }

            TextField(
                value = text,
                onValueChange = {
                    if (it.length < 32) {
                        onTextChanged(it)
                    }
                },
                label = { Text(text = stringResource(id = R.string.text_title)) },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold
                ),
                shape = RectangleShape,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = text,
        onValueChange = onTextChanged,
        label = { Text(text = stringResource(id = R.string.text_content)) },
        modifier = modifier,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            localeList = LocaleList.current,
            textDirection = TextDirection.ContentOrRtl,
            fontFamily = FontFamily.SansSerif
        ),
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )
}

@Composable
fun EditAndDeleteButtons(
    deleteNote: () -> Unit,
    saveNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        DeleteTextFab(
            onDeleteClick = deleteNote,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
        )
        SaveTextFab(
            onSaveClick = saveNote,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp, end = 16.dp)
        )
    }

}

@Composable
fun SaveTextFab(
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        onClick = { onSaveClick() },
        icon = {
            Icon(
                Icons.Filled.Done, contentDescription = stringResource(id = R.string.save_text)
            )
        },
        text = { Text(text = stringResource(id = R.string.save_text)) },
        modifier = modifier,
    )
}


@Composable
fun DeleteTextFab(
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        onClick = { onDeleteClick() },
        icon = {
            Icon(
                Icons.Filled.Delete, contentDescription = stringResource(id = R.string.delete_text)
            )
        },
        text = { Text(text = stringResource(id = R.string.delete_text)) },
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError,
        modifier = modifier,
    )
}

@Preview
@Composable
fun TopBarPreview() {
    KryptTheme {
        Surface {
            TopBar(text = "Title", onTextChanged = {}, onNavigationClickIcon = {})
        }
    }
}

@Preview
@Composable
fun ContentTextFieldPreview() {
    KryptTheme {
        Surface {
            ContentTextField(
                text = "Content",
                onTextChanged = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
        }
    }
}

@Preview
@Composable
fun EditAndDeleteButtonsPreview() {
    KryptTheme {
        Surface {
            EditAndDeleteButtons(deleteNote = { /*TODO*/ }, saveNote = { /*TODO*/ })
        }
    }
}