package ir.mehdiyari.krypt.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.shared.designsystem.components.PasswordTextField

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
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.krypt),
            contentDescription = stringResource(id = R.string.splash_content_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(100.dp)
        )

        DropDownTextField(
            items = usernames,
            selectedItem = selectedUserName,
            onItemChanged = onUserNameChanged,
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 5.dp)
        )

        PasswordTextField(
            password = password,
            hint = stringResource(id = R.string.account_password),
            onPasswordChanged = onPasswordChanged,
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 5.dp, bottom = 5.dp)
        )

        Spacer(modifier = modifier.size(100.dp))
    }
}

@Composable
@Preview
fun CrateAccountButton(
    onCreateAccountClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        onClick = { onCreateAccountClick() },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(text = stringResource(id = R.string.button_create_new_account))
    }
}

@Composable
@Preview
fun LoginButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
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
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
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
    ExposedDropdownMenuBox(modifier = modifier.fillMaxWidth(),
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }) {

        TextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = selectedItem,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            label = { Text(text = stringResource(id = R.string.account_name)) },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        )

        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }) {
            items.forEach { item ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(text = item) },
                    onClick = {
                        onItemChanged(item)
                        expanded = false
                    })
            }
        }
    }
}

@Preview
@Composable
fun LoginFieldsPreview() {
    KryptTheme {
        Surface {
            LoginFields(usernames = List(5) { "UserName$it" },
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