package ir.mehdiyari.krypt.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ir.mehdiyari.krypt.ui.home.ROUTE_HOME
import ir.mehdiyari.krypt.ui.login.ROUTE_LOGIN
import ir.mehdiyari.krypt.ui.logout.ROUTE_CREATE_ACCOUNT
import kotlinx.coroutines.CoroutineScope

val authDestinations = setOf(ROUTE_LOGIN, ROUTE_CREATE_ACCOUNT)

@Stable
class KryptAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope
) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isInHomeRoute: Boolean
        @Composable get() = currentDestination?.route == ROUTE_HOME


}

@Composable
fun rememberKryptAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): KryptAppState = remember(navController, coroutineScope) {
    KryptAppState(navController, coroutineScope)
}