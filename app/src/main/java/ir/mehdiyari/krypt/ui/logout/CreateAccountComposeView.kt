package ir.mehdiyari.krypt.ui.logout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
@Preview
fun CreateAccountComposeScreen(
    onClick: (username: String, password: String) -> Unit = { _, _ -> }
) {
    KryptTheme {
        val nameValue = remember { mutableStateOf(TextFieldValue()) }
        val passwordValue = remember { mutableStateOf(TextFieldValue()) }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Image(
                painter = painterResource(R.drawable.krypt),
                contentDescription = stringResource(id = R.string.splash_content_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = stringResource(id = R.string.create_account),
                color = MaterialTheme.colors.onBackground,
                modifier = Modifier.padding(20.dp, 20.dp, 20.dp, 4.dp),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            TextField(
                singleLine = true,
                value = nameValue.value,
                onValueChange = {
                    nameValue.value = it
                },
                label = { Text(stringResource(id = R.string.account_name)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 20.dp, 20.dp, 3.dp)
                    .padding(20.dp, 0.dp, 20.dp, 3.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrect = false,
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle(color = MaterialTheme.colors.onBackground)
            )

            TextField(
                singleLine = true,
                value = passwordValue.value,
                onValueChange = {
                    passwordValue.value = it
                },
                label = { Text(stringResource(id = R.string.account_password)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 3.dp, 20.dp, 3.dp)
                    .padding(20.dp, 3.dp, 20.dp, 3.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    autoCorrect = false,
                    imeAction = ImeAction.Go
                ),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = TextStyle(color = MaterialTheme.colors.onBackground),
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(35.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = { onClick(nameValue.value.text, passwordValue.value.text) },
                icon = {
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = stringResource(id = R.string.button_create_account)
                    )
                },
                text = { Text(text = stringResource(id = R.string.button_create_account)) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    }
}