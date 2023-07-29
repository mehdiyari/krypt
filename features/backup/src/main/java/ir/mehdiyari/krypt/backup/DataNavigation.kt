package ir.mehdiyari.krypt.backup

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ROUTE_DATA = "data"

fun NavController.navigateToData() {
    this.navigate(ROUTE_DATA)
}

fun NavGraphBuilder.dataScreen(onBackPressed: () -> Unit) {
    composable(ROUTE_DATA) {
        DataRoute(onNavigationClicked = onBackPressed, modifier = Modifier)
    }
}