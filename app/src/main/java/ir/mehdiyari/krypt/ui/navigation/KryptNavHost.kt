package ir.mehdiyari.krypt.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import ir.mehdiyari.krypt.ui.login.loginScreen
import ir.mehdiyari.krypt.ui.login.navigateToLogin
import ir.mehdiyari.krypt.ui.splash.ROUTE_SPLASH
import ir.mehdiyari.krypt.ui.splash.splashScreen

@Composable
fun KryptNaveHost(
    navController: NavHostController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = ROUTE_SPLASH
) {

    NavHost(
        modifier = modifier,
        navController = navController, startDestination = ROUTE_SPLASH
    ) {
        splashScreen(accountExists = {
            navController.navigateToLogin()
        }, noAccountExists = {})

        loginScreen(onCreateAccountClicked = {}, onLoginSuccess = {}, showSnackBar = onShowSnackbar)

    }
}

