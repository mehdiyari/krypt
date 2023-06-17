package ir.mehdiyari.krypt.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.mehdiyari.krypt.ui.media.MediaFragmentAction
import ir.mehdiyari.krypt.ui.media.SharedMediaListModel

const val ROUTE_HOME = "home"

fun NavController.navigateToHome() {
    this.navigate(ROUTE_HOME)
}

fun NavGraphBuilder.homeScreen(
    openTextsScreen: (String?) -> Unit,
    openMusicAndAudioScreen: () -> Unit,
    openMediaScreen: (MediaFragmentAction, SharedMediaListModel?) -> Unit,
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