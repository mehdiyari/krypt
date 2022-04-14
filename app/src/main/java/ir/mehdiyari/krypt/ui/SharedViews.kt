package ir.mehdiyari.krypt.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.R

@Composable
fun PasswordTextField(passwordValue: MutableState<TextFieldValue>) {
    TextField(
        singleLine = true,
        value = passwordValue.value,
        onValueChange = {
            passwordValue.value = it
        },
        label = { Text(stringResource(id = R.string.account_password)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp, 3.dp, 25.dp, 3.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrect = false,
            imeAction = ImeAction.Go
        ),
        visualTransformation = PasswordVisualTransformation(),
        textStyle = TextStyle(color = MaterialTheme.colors.onBackground),
    )
}