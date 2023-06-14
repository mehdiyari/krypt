package ir.mehdiyari.krypt.ui.logout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.PasswordTextField
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun CreateAccountComposeScreen(
    viewModel: CreateAccountViewModel = viewModel()
) {
    KryptTheme {
        val nameValue = remember { mutableStateOf(TextFieldValue()) }
        var passwordValue by remember { mutableStateOf("") }
        var confirmPasswordValue by remember { mutableStateOf("") }

        CreateAccountItems(nameValue, passwordValue, onPasswordChanged =  {passwordValue = it}, confirmPassword = confirmPasswordValue, onConfirmPasswordChanged = {confirmPasswordValue = it})
        CreateAccountButton {
            viewModel.addAccount(
                nameValue.value.text,
                passwordValue,
                confirmPasswordValue
            )
        }
    }
}

@Composable
@Preview
private fun CreateAccountButton(
    createAccountClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(20.dp)
    ) {
        val context = LocalContext.current
        ExtendedFloatingActionButton(
            onClick = {
                createAccountClick.invoke()
            },
            icon = {
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.button_create_account)
                )
            },
            text = { Text(text = stringResource(id = R.string.button_create_account)) },
            containerColor =  MaterialTheme.colorScheme.primary,
            contentColor =  MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun CreateAccountItems(
    nameValue: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("Test1")),
    password:String,
    onPasswordChanged: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChanged: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(bottom = 80.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.krypt),
            contentDescription = stringResource(id = R.string.splash_content_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = stringResource(id = R.string.create_account),
            color =  MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(25.dp, 20.dp, 25.dp, 4.dp),
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
                .padding(25.dp, 20.dp, 25.dp, 3.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                autoCorrect = false,
                imeAction = ImeAction.Next
            ),
            textStyle = TextStyle(color =  MaterialTheme.colorScheme.onBackground)
        )

        PasswordTextField(password, onPasswordChanged = onPasswordChanged)
        PasswordTextField(confirmPassword, onPasswordChanged = onConfirmPasswordChanged ,hintString = R.string.password_confirmation)
    }
}

@Composable
@Preview
fun CreateAccountScreenPreview() {
    KryptTheme {
//        CreateAccountItems()
        CreateAccountButton()
    }
}