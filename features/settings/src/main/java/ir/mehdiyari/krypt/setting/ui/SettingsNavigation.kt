package ir.mehdiyari.krypt.setting.ui

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ROUTE_SETTINGS = "settings"

fun NavController.navigateToSettings() {
    navigate(ROUTE_SETTINGS)
}

fun NavGraphBuilder.settingsRoute(
    onBackPressed: () -> Unit
) {
    composable(ROUTE_SETTINGS) {
        SettingsRoute(modifier = Modifier, onNavigationClickIcon = onBackPressed)
    }
}