package ir.mehdiyari.krypt.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.PasswordTextField
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun LoginFields(
    usernames: List<String>,
    selectedUserName: String,
    onUserNameChanged: (String) -> Unit,
    password: String,
    onPasswordChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.krypt),
            contentDescription = stringResource(id = R.string.splash_content_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(100.dp)
        )

        DropDownTextField(
            items = usernames,
            selectedItem = selectedUserName,
            onItemChanged = onUserNameChanged
        )

        PasswordTextField(password, onPasswordChanged)
    }
}

@Composable
fun CrateAccountButton(onCreateAccountClick: () -> Unit, modifier: Modifier) {
    ExtendedFloatingActionButton(
        onClick = { onCreateAccountClick() },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        modifier = modifier
    ) {
        Text(text = stringResource(id = R.string.button_create_new_account))
    }
}

@Composable
fun LoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            Icon(
                Icons.Filled.ArrowForward,
                contentDescription = stringResource(id = R.string.button_login)
            )
        },
        text = { Text(text = stringResource(id = R.string.button_login)) },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownTextField(
    items: List<String>,
    selectedItem: String,
    onItemChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(modifier = modifier, expanded = expanded, onExpandedChange = {
        expanded = !expanded
    }) {
        TextField(
            value = selectedItem,
            onValueChange = { },
            label = { Text(text = stringResource(id = R.string.account_name)) },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = {
                        onItemChanged(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginFieldsPreview() {
    KryptTheme {
        Surface {
            LoginFields(
                usernames = List(5) { "UserName$it" },
                selectedUserName = "UserName0",
                onUserNameChanged = {},
                password = "12345",
                onPasswordChanged = {})
        }
    }
}

@Preview
@Composable
private fun DropDownTextFieldPreview(@PreviewParameter(UserNamesPreviewParameterProvider::class) usernames: List<String>) {


    KryptTheme {
        Surface {
            DropDownTextField(items = usernames, usernames[0], {})
        }
    }
}

class UserNamesPreviewParameterProvider : PreviewParameterProvider<List<String>> {
    override val values: Sequence<List<String>>
        get() = sequenceOf(List(5) { "UserName$it" })

}