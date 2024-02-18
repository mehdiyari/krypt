package ir.mehdiyari.krypt.features.auth.login

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.mehdiyari.krypt.shared.designsystem.components.snackbar.KryptSnackBar

const val ROUTE_LOGIN = "login"

fun NavController.navigateToLogin() {
    this.navigate(ROUTE_LOGIN)
}

fun NavGraphBuilder.loginScreen(
    onCreateAccountClicked: () -> Unit,
    onLoginSuccess: () -> Unit,
    showSnackBar: (KryptSnackBar) -> Unit,
    onRestoreClicked: () -> Unit,
) {

    composable(ROUTE_LOGIN) {
        LoginRoute(
            onCreateAccountClicked = onCreateAccountClicked,
            onLoginSuccess = onLoginSuccess,
            showSnackBar = showSnackBar,
            modifier = Modifier.fillMaxSize(),
            onRestoreClicked = onRestoreClicked,
        )
    }

}