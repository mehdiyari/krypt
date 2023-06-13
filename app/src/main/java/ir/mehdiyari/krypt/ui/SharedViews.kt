package ir.mehdiyari.krypt.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintString: Int = R.string.account_password,
) {
    TextField(
        singleLine = true,
        value = password,
        onValueChange = onPasswordChanged,
        label = { Text(stringResource(id = hintString)) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrect = false,
            imeAction = ImeAction.Go
        ),
        visualTransformation = PasswordVisualTransformation(),
        textStyle = TextStyle(color = MaterialTheme.colors.onBackground),
    )
}

@Preview
@Composable
fun PasswordTextFieldEmptyPreview() {
    KryptTheme {
        Surface {
            PasswordTextField(
                password = "",
                onPasswordChanged = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp, 3.dp, 25.dp, 3.dp)
            )
        }
    }
}

@Preview
@Composable
fun PasswordTextFieldFilledPreview() {
    KryptTheme {
        Surface {
            PasswordTextField(
                password = "123456",
                onPasswordChanged = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp, 3.dp, 25.dp, 3.dp)
            )
        }
    }
}