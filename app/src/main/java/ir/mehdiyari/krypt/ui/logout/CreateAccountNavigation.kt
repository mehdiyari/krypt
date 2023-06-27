package ir.mehdiyari.krypt.ui.logout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.mehdiyari.krypt.ui.splash.ROUTE_SPLASH

const val ROUTE_CREATE_ACCOUNT = "create_account"

fun NavController.navigateToCreateAccount() {
    this.navigate(ROUTE_CREATE_ACCOUNT) {
        popUpTo(ROUTE_SPLASH) {
            inclusive = true
        }
    }
}

fun NavGraphBuilder.createAccountScreen(
    onCreateAccountSuccess: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    composable(ROUTE_CREATE_ACCOUNT) {
        CreateAccountRoute(
            onLoginSuccess = onCreateAccountSuccess,
            onShowSnackbar = onShowSnackbar,
            modifier = Modifier.fillMaxSize()
        )
    }
}