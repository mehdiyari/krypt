package ir.mehdiyari.krypt.features.auth.create_account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.mehdiyari.krypt.shared.designsystem.components.snackbar.KryptSnackBar

const val ROUTE_CREATE_ACCOUNT = "create_account"

fun NavController.navigateToCreateAccount() {
    this.navigate(ROUTE_CREATE_ACCOUNT) {
        popBackStack()
    }
}

fun NavGraphBuilder.createAccountScreen(
    onCreateAccountSuccess: () -> Unit,
    onShowSnackbar: (KryptSnackBar) -> Unit,
    onRestoreClicked: () -> Unit,
) {
    composable(ROUTE_CREATE_ACCOUNT) {
        CreateAccountRoute(
            onLoginSuccess = onCreateAccountSuccess,
            onShowSnackbar = onShowSnackbar,
            modifier = Modifier.fillMaxSize(),
            onRestoreClicked = onRestoreClicked,
        )
    }
}