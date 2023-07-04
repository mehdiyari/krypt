package ir.mehdiyari.krypt.ui.splash

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ROUTE_SPLASH = "splash"

fun NavGraphBuilder.splashScreen(
    accountExists: () -> Unit,
    noAccountExists: () -> Unit
) {
    composable(ROUTE_SPLASH) {
        SplashRoute(
            accountExists = accountExists,
            noAccountExists = noAccountExists,
            modifier = Modifier.fillMaxSize()
        )
    }
}