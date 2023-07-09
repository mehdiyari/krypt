package ir.mehdiyari.krypt.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.mehdiyari.krypt.ui.media.MediaViewAction
import ir.mehdiyari.krypt.ui.media.SharedMediaListModel

const val ROUTE_HOME = "home"

fun NavController.navigateToHome() {
    this.navigate(ROUTE_HOME) {
        popBackStack()
    }
}

fun NavGraphBuilder.homeScreen(
    openTextsScreen: (String?) -> Unit,
    openMusicAndAudioScreen: () -> Unit,
    openMediaScreen: (MediaViewAction, SharedMediaListModel?) -> Unit,
) {
    composable(ROUTE_HOME) {
        HomeRoute(
            openTextsScreen = openTextsScreen,
            openMusicAndAudioScreen = openMusicAndAudioScreen,
            openMediaScreen = openMediaScreen,
            modifier = Modifier.fillMaxSize()
        )
    }
}