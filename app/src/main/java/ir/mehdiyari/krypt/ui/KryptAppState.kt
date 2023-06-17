package ir.mehdiyari.krypt.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.mehdiyari.krypt.ui.login.ROUTE_LOGIN
import ir.mehdiyari.krypt.ui.logout.ROUTE_CREATE_ACCOUNT
import ir.mehdiyari.krypt.ui.splash.ROUTE_SPLASH

val authDestinations = setOf(ROUTE_SPLASH, ROUTE_LOGIN, ROUTE_CREATE_ACCOUNT)

@Stable
class KryptAppState(
    val navController: NavHostController,
) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isInAuthFlow: Boolean
        @Composable get() = authDestinations.contains(currentDestination?.route)


}

@Composable
fun rememberKryptAppState(
    navController: NavHostController = rememberNavController()
): KryptAppState = remember(navController) {
    KryptAppState(navController)
}