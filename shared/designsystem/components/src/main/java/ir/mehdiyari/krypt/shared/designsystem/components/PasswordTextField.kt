package ir.mehdiyari.krypt.shared.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme

@Composable
fun PasswordTextField(
    password: String,
    hint: String,
    onPasswordChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        singleLine = true,
        value = password,
        onValueChange = onPasswordChanged,
        label = { Text(hint) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrect = false,
            imeAction = ImeAction.Go
        ),
        visualTransformation = PasswordVisualTransformation(),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
    )
}

@Preview
@Composable
private fun PasswordTextFieldEmptyPreview() {
    KryptTheme {
        Surface {
            PasswordTextField(
                password = "",
                hint = "Password",
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
private fun PasswordTextFieldFilledPreview() {
    KryptTheme {
        Surface {
            PasswordTextField(
                password = "123456",
                hint = "Password",
                onPasswordChanged = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp, 3.dp, 25.dp, 3.dp)
            )
        }
    }
}