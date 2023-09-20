package ir.mehdiyari.krypt.features.auth.create_account

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.createAccount.R

@Composable
internal fun CreateAccountRoute(
    onLoginSuccess: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    viewModel: CreateAccountViewModel = hiltViewModel(),
    onRestoreClicked: () -> Unit,
) {

    val createAccountUiState by viewModel.createAccountViewState.collectAsStateWithLifecycle(null)

    val context = LocalContext.current

    LaunchedEffect(key1 = createAccountUiState) {
        when (createAccountUiState) {
            is CreateAccountViewState.SuccessCreateAccount -> {
                val message = context.getString(R.string.successfully_create_account)

                Toast.makeText(context, message, Toast.LENGTH_SHORT)
                    .show()
                onLoginSuccess()
            }

            is CreateAccountViewState.FailureCreateAccount -> {
                onShowSnackbar(
                    context.getString((createAccountUiState as CreateAccountViewState.FailureCreateAccount).errorResId),
                    null
                )
            }

            null -> Unit
        }
    }


    CreateAccountScreen(
        onCreateAccountClicked = { userName, password, confirmPassword ->
            viewModel.addAccount(userName, password, confirmPassword)
        },
        modifier = modifier,
        onRestoreClicked = onRestoreClicked,
    )
}

@Composable
private fun CreateAccountScreen(
    onCreateAccountClicked: (userName: String, password: String, confirmPassword: String) -> Unit,
    modifier: Modifier = Modifier,
    onRestoreClicked: () -> Unit,
) {
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        CreateAccountItems(
            userName = userName,
            onUserNameChanged = {
                userName = it
            },
            password = password,
            onPasswordChanged = { password = it },
            confirmPassword = confirmPassword,
            onConfirmPasswordChanged = { confirmPassword = it },
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
                .padding(start = 20.dp, end = 20.dp),
            onRestoreClicked = onRestoreClicked,
        )

        ExtendedFloatingActionButton(
            onClick = {
                onCreateAccountClicked(userName, password, confirmPassword)
            },
            icon = {
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.button_create_account)
                )
            },
            text = { Text(text = stringResource(id = R.string.button_create_account)) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
        )
    }
}

@Preview
@Composable
private fun CreateAccountScreenPreview() {
    KryptTheme {
        Surface {
            CreateAccountScreen(
                onCreateAccountClicked = { userName, password, confirmPassword -> },
                modifier = Modifier.fillMaxSize(),
                onRestoreClicked = {},
            )
        }
    }
}