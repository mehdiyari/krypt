package ir.mehdiyari.krypt.restore

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ROUTE_RESTORE = "restore"

fun NavController.navigateToRestore() {
    this.navigate(ROUTE_RESTORE)
}

fun NavGraphBuilder.restoreScreen(onBackPressed: () -> Unit) {
    composable(ROUTE_RESTORE) {
        RestoreRoute(modifier = Modifier, onBackPressed = onBackPressed)
    }
}