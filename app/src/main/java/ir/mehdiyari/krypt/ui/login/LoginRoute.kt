package ir.mehdiyari.krypt.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun LoginRoute(
    onCreateAccountClicked: () -> Unit,
    onLoginSuccess: () -> Unit,
    showSnackBar: suspend (message: String, action: String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {

    val userNames by viewModel.allUserNamesState.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle(null)

    when (loginState) {
        LoginViewState.SuccessfulLogin -> onLoginSuccess()
        is LoginViewState.FailureLogin -> {
            val message =
                LocalContext.current.getString((loginState as LoginViewState.FailureLogin).errorId)
            LaunchedEffect(key1 = loginState) {
                showSnackBar(message, null)
            }
        }

        null -> Unit
    }

    LoginScreen(
        userNames,
        onLoginClicked = { userName, password ->
            viewModel.login(userName, password)
        },
        onCreateAccountClicked = onCreateAccountClicked,
        modifier = modifier
    )
}

@Composable
fun LoginScreen(
    accounts: List<String>,
    onLoginClicked: (userName: String, password: String) -> Unit,
    onCreateAccountClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var userName by remember { mutableStateOf(accounts[0]) }
    var password by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        LoginFields(
            usernames = accounts,
            selectedUserName = userName,
            onUserNameChanged = { userName = it },
            password = password,
            onPasswordChanged = { password = it },
            modifier = Modifier
                .align(Alignment.Center)
        )

        LoginButton(
            onClick = { onLoginClicked(userName, password) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        CrateAccountButton(
            onCreateAccountClick = onCreateAccountClicked,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    KryptTheme {
        Surface {
            LoginScreen(
                accounts = List(5) { "UserName$it" },
                onLoginClicked = { _, _ -> },
                onCreateAccountClicked = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}