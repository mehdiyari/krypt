package ir.mehdiyari.krypt.ui.text.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
@Preview
fun AddTextComposeView(
    viewModel: AddTextViewModel = viewModel(),
    onNavigationClickIcon: () -> Unit = {}
) {
    KryptTheme {
        val argsState = viewModel.argsTextViewState.collectAsState().value
        if (argsState is AddTextArgsViewState.Error) {
            return@KryptTheme
        }

        val textTitleField = remember { mutableStateOf(TextFieldValue()) }
        val textContentField = remember { mutableStateOf(TextFieldValue()) }

        val isPreviewMode = argsState is AddTextArgsViewState.TextArg

        if (isPreviewMode) {
            argsState as AddTextArgsViewState.TextArg
            textTitleField.value = TextFieldValue(argsState.textEntity.title)
            textContentField.value = TextFieldValue(argsState.textEntity.content)
        }

        Scaffold(
            topBar = {
                TopBarSurface(
                    onNavigationClickIcon,
                    textTitleField,
                    isPreviewMode = isPreviewMode
                )
            }, content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    ContentTextField(
                        textContentField,
                        isPreviewMode = isPreviewMode
                    )
                }
            })

        if (!isPreviewMode) {
            SaveTextFab {
                viewModel.saveNote(textTitleField.value.text.trim(), textContentField.value.text)
            }
        } else {
            DeleteTextFab {
                viewModel.deleteNote()
            }
        }
    }
}

@Composable
private fun TopBarSurface(
    onNavigationClickIcon: () -> Unit,
    textTitleField: MutableState<TextFieldValue>,
    isPreviewMode: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            IconButton(modifier = Modifier
                .fillMaxHeight()
                .padding(0.dp),
                onClick = {
                    onNavigationClickIcon()
                }) {
                Icon(Icons.Filled.ArrowBack, "")
            }

            TextField(
                value = textTitleField.value,
                onValueChange = {
                    if (it.text.length < 32) {
                        textTitleField.value = it
                    }
                },
                label = { Text(text = stringResource(id = R.string.text_title)) },
                modifier = Modifier
                    .fillMaxHeight(),
                textStyle = TextStyle(
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold
                ),
                shape = RectangleShape,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = MaterialTheme.colors.onSurface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                readOnly = isPreviewMode
            )
        }
    }
}

@Composable
private fun ContentTextField(
    textContentField: MutableState<TextFieldValue>,
    isPreviewMode: Boolean
) {
    TextField(
        value = textContentField.value,
        onValueChange = { textContentField.value = it },
        label = { Text(text = stringResource(id = R.string.text_content)) },
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = if (isPreviewMode) 0.dp else 55.dp),
        textStyle = TextStyle(
            color = MaterialTheme.colors.onBackground,
            fontSize = 18.sp,
            localeList = LocaleList.current,
            textDirection = TextDirection.ContentOrRtl,
            fontFamily = FontFamily.SansSerif
        ),
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            textColor = MaterialTheme.colors.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        readOnly = isPreviewMode
    )
}

@Composable
private fun SaveTextFab(
    onSaveClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(15.dp)
    ) {
        ExtendedFloatingActionButton(
            onClick = { onSaveClick() },
            icon = {
                Icon(
                    Icons.Filled.Done,
                    contentDescription = stringResource(id = R.string.save_text)
                )
            },
            text = { Text(text = stringResource(id = R.string.save_text)) },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        )
    }
}


@Composable
private fun DeleteTextFab(
    onDeleteClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(15.dp)
    ) {
        ExtendedFloatingActionButton(
            onClick = { onDeleteClick() },
            icon = {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(id = R.string.delete_text)
                )
            },
            text = { Text(text = stringResource(id = R.string.delete_text)) },
            backgroundColor = MaterialTheme.colors.error,
            contentColor = MaterialTheme.colors.onError
        )
    }
}