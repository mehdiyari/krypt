package ir.mehdiyari.krypt.ui.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ROUTE_LOGIN = "login"

fun NavController.navigateToLogin() {
    this.navigate(ROUTE_LOGIN)
}

fun NavGraphBuilder.loginScreen(
    onCreateAccountClicked: () -> Unit,
    onLoginSuccess: () -> Unit,
    showSnackBar: suspend (message: String, action: String?) -> Boolean,
) {

    composable(ROUTE_LOGIN) {
        LoginRoute(
            onCreateAccountClicked = onCreateAccountClicked,
            onLoginSuccess = onLoginSuccess,
            showSnackBar = showSnackBar
        )
    }

}