package ir.mehdiyari.krypt.ui.text.add

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
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
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddTextComposeView(
    viewModel: AddTextViewModel = viewModel(),
    sharedContentText: String? = null,
    onNavigationClickIcon: () -> Unit = {}
) {
    KryptTheme {
        val argsState = viewModel.argsTextViewState.collectAsState().value
        if (argsState is AddTextArgsViewState.Error) {
            return@KryptTheme
        }

        val textTitleField = rememberSaveable { mutableStateOf("") }
        val textContentField = rememberSaveable { mutableStateOf("") }

        val isEditMode = argsState is AddTextArgsViewState.TextArg

        if (isEditMode && textContentField.value.isEmpty() && textTitleField.value.isEmpty()) {
            argsState as AddTextArgsViewState.TextArg
            textTitleField.value = argsState.textEntity.title
            textContentField.value = argsState.textEntity.content
        } else {
            if (!sharedContentText.isNullOrBlank()) {
                textContentField.value = sharedContentText
            }
        }

        Scaffold(topBar = {
            TopBarSurface(
                onNavigationClickIcon,
                textTitleField,
            )
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                ContentTextField(
                    textContentField,
                )
            }
        })

        if (!isEditMode) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.padding(15.dp)
            ) {
                SaveTextFab {
                    viewModel.saveNote(
                        textTitleField.value.trim(),
                        textContentField.value
                    )
                }
            }
        } else {
            EditAndDeleteButtons(
                textTitleField, textContentField, viewModel::deleteNote, viewModel::saveNote
            )
        }
    }
}

@Composable
@Preview
private fun EditAndDeleteButtons(
    textTitleField: MutableState<String> = mutableStateOf("Title"),
    textContentField: MutableState<String> = mutableStateOf("Content"),
    deleteNote: (() -> Unit)? = null,
    saveNote: ((title: String, context: String) -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(15.dp)
    ) {
        Row {
            DeleteTextFab(modifier = Modifier.padding(4.dp)) {
                deleteNote?.invoke()
            }

            SaveTextFab(modifier = Modifier.padding(4.dp)) {
                saveNote?.invoke(textTitleField.value.trim(), textContentField.value)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun TopBarSurface(
    onNavigationClickIcon: () -> Unit = {},
    textTitleField: MutableState<String> = mutableStateOf("Test"),
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp), shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            IconButton(modifier = Modifier
                .fillMaxHeight()
                .padding(0.dp), onClick = {
                onNavigationClickIcon()
            }) {
                Icon(Icons.Filled.ArrowBack, "")
            }

            TextField(
                value = textTitleField.value,
                onValueChange = {
                    if (it.length < 32) {
                        textTitleField.value = it
                    }
                },
                label = { Text(text = stringResource(id = R.string.text_title)) },
                modifier = Modifier.fillMaxHeight(),
                textStyle = TextStyle(
                    color =  MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold
                ),
                shape = RectangleShape,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedTextColor =  MaterialTheme.colorScheme.onSurface,
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
@Preview
private fun ContentTextField(
    textContentField: MutableState<String> = mutableStateOf(("Test")),
) {
    TextField(
        value = textContentField.value,
        onValueChange = { textContentField.value = it },
        label = { Text(text = stringResource(id = R.string.text_content)) },
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = 55.dp),
        textStyle = TextStyle(
            color =  MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            localeList = LocaleList.current,
            textDirection = TextDirection.ContentOrRtl,
            fontFamily = FontFamily.SansSerif
        ),
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedTextColor =  MaterialTheme.colorScheme.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )
}

@Composable
@Preview
private fun SaveTextFab(
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit = {},
) {
    ExtendedFloatingActionButton(
        onClick = { onSaveClick() },
        icon = {
            Icon(
                Icons.Filled.Done, contentDescription = stringResource(id = R.string.save_text)
            )
        },
        text = { Text(text = stringResource(id = R.string.save_text)) },
        containerColor =  MaterialTheme.colorScheme.primary,
        contentColor =  MaterialTheme.colorScheme.onPrimary,
        modifier = modifier,
    )
}


@Composable
@Preview
private fun DeleteTextFab(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit = {},
) {
    ExtendedFloatingActionButton(
        onClick = { onDeleteClick() },
        icon = {
            Icon(
                Icons.Filled.Delete, contentDescription = stringResource(id = R.string.delete_text)
            )
        },
        text = { Text(text = stringResource(id = R.string.delete_text)) },
        containerColor =  MaterialTheme.colorScheme.error,
        contentColor =  MaterialTheme.colorScheme.onError,
        modifier = modifier,
    )
}