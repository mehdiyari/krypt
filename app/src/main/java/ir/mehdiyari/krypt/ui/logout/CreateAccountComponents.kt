package ir.mehdiyari.krypt.ui.logout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.PasswordTextField
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun CreateAccountItems(
    userName: String,
    onUserNameChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.krypt),
            contentDescription = stringResource(id = R.string.splash_content_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = stringResource(id = R.string.create_account),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(25.dp, 20.dp, 25.dp, 4.dp),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        TextField(
            singleLine = true,
            value = userName,
            onValueChange = onUserNameChanged,
            label = { Text(stringResource(id = R.string.account_name)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                autoCorrect = false,
                imeAction = ImeAction.Next
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )

        PasswordTextField(
            password,
            onPasswordChanged = onPasswordChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        PasswordTextField(
            confirmPassword,
            onPasswordChanged = onConfirmPasswordChanged,
            hintString = R.string.password_confirmation,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Spacer(modifier = modifier.size(100.dp))
    }
}

@Composable
@Preview
fun CreateAccountItemsPreview() {
    KryptTheme {
        Surface {
            CreateAccountItems("Mohammad", {}, "123456", {}, "123456", {})
        }
    }
}