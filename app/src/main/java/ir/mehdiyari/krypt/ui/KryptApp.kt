package ir.mehdiyari.krypt.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ir.mehdiyari.krypt.ui.login.loginScreen
import ir.mehdiyari.krypt.ui.login.navigateToLogin
import ir.mehdiyari.krypt.ui.splash.ROUTE_SPLASH
import ir.mehdiyari.krypt.ui.splash.splashScreen

@Composable
fun KryptApp() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ROUTE_SPLASH) {
        splashScreen(accountExists = {
            navController.navigateToLogin()
        }, noAccountExists = {})

        loginScreen(onCreateAccountClicked = {}, onLoginSuccess = {}, showSnackBar = {})


    }

}