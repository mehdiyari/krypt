package ir.mehdiyari.krypt.ui.login

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
fun LoginRoute(
    onCreateAccountClicked: () -> Unit,
    onLoginSuccess: () -> Unit,
    showSnackBar: (Int) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val userNames by viewModel.allUserNamesState.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle(null)

    DisposableEffect(key1 = loginState){
        loginState?.let {
            if (it is LoginViewState.SuccessfulLogin) {
                onLoginSuccess()
            } else if (it is LoginViewState.FailureLogin) {
                showSnackBar(it.errorId)
            }
        }
        onDispose {  }
    }

    LoginScreen(
        userNames,
        onLoginClicked = { userName, password ->
            viewModel.login(userName, password)
        }, onCreateAccountClicked = onCreateAccountClicked
    )
}

@Composable
fun LoginScreen(
    accounts: List<String>,
    onLoginClicked: (userName: String, password: String) -> Unit,
    onCreateAccountClicked: () -> Unit,
) {
    var userName by remember { mutableStateOf(accounts[0]) }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginFields(
            usernames = accounts,
            selectedUserName = userName,
            onUserNameChanged = { userName = it },
            password = password,
            onPasswordChanged = { password = it },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 65.dp)
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

@Preview(device = "id:pixel_xl")
@Composable
private fun LoginScreenPreview() {
    KryptTheme {
        Surface {
            LoginScreen(
                accounts = List(5) { "UserName$it" },
                onLoginClicked = { _, _ -> },
                onCreateAccountClicked = {})
        }
    }
}