package ir.mehdiyari.krypt.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme

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
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
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

@Composable
fun ManageExternalPermissionDialog(
    modifier: Modifier,
    state: MutableState<Boolean>,
    onGrantPermissionClicked: () -> Unit,
) {
    if (state.value) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = {
                state.value = false
            },
            title = {
                Text(text = stringResource(id = R.string.manager_external_permission))
            },
            text = {
                Column {
                    Text(
                        modifier = modifier.padding(bottom = 10.dp),
                        text = stringResource(id = R.string.manager_external_permission_description)
                    )
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        onGrantPermissionClicked.invoke()
                    },
                ) {
                    Text(stringResource(id = R.string.YES))
                }

            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        state.value = false
                    }
                ) {
                    Text(stringResource(id = R.string.NO))
                }
            })
    }

}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun ManageExternalPermissionDialogPreview() {
    KryptTheme {
        ManageExternalPermissionDialog(modifier = Modifier, state = mutableStateOf(true)) {

        }
    }
}