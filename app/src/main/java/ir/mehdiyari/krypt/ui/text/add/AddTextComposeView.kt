package ir.mehdiyari.krypt.ui.text.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
        val textTitleField = remember { mutableStateOf(TextFieldValue()) }
        val textContentField = remember { mutableStateOf(TextFieldValue()) }
        Scaffold(
            topBar = {
                TopBarSurface(onNavigationClickIcon, textTitleField)
            }, content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    ContentTextField(textContentField)
                }
            })

        SaveTextFab()
    }
}

@Composable
private fun TopBarSurface(
    onNavigationClickIcon: () -> Unit,
    textTitleField: MutableState<TextFieldValue>
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
                singleLine = true
            )
        }
    }
}

@Composable
private fun ContentTextField(textContentField: MutableState<TextFieldValue>) {
    TextField(
        value = textContentField.value,
        onValueChange = { textContentField.value = it },
        label = { Text(text = stringResource(id = R.string.text_content)) },
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(bottom = 55.dp),
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
        )
    )
}

@Composable
private fun SaveTextFab() {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(15.dp)
    ) {
        ExtendedFloatingActionButton(
            onClick = { },
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