package ir.mehdiyari.krypt.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.mehdiyari.krypt.ui.media.MediaViewAction
import ir.mehdiyari.krypt.ui.media.SharedMediaListModel
import ir.mehdiyari.krypt.ui.splash.ROUTE_SPLASH

const val ROUTE_HOME = "home"

fun NavController.navigateToHome() {
    this.navigate(ROUTE_HOME) {
        popUpTo(ROUTE_SPLASH) {
            inclusive = true
        }
    }
}

fun NavGraphBuilder.homeScreen(
    openTextsScreen: (String?) -> Unit,
    openMusicAndAudioScreen: () -> Unit,
    openMediaScreen: (MediaViewAction, SharedMediaListModel?) -> Unit,
    openAudioRecorderScreen: () -> Unit,
    onShowSnackbar: suspend (String, String) -> Boolean,
) {
    composable(ROUTE_HOME) {
        HomeRoute(
            openTextsScreen = openTextsScreen,
            openMusicAndAudioScreen = openMusicAndAudioScreen,
            openMediaScreen = openMediaScreen,
            openAudioRecorderScreen = openAudioRecorderScreen,
            onShowSnackbar = onShowSnackbar,
            modifier = Modifier.fillMaxSize()
        )
    }
}