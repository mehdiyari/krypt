package ir.mehdiyari.krypt.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ir.mehdiyari.krypt.ui.media.MediaViewAction

const val ROUTE_HOME = "home"

fun NavController.navigateToHome() {
    this.navigate(ROUTE_HOME) {
        popBackStack()
    }
}

fun NavGraphBuilder.homeScreen(
    openAddTextScreen: (String?) -> Unit,
    openTextsScreen: () -> Unit,
    openMusicAndAudioScreen: () -> Unit,
    openMediaScreen: (MediaViewAction) -> Unit,
    sharedDataViewModel: ShareDataViewModel,
) {
    composable(ROUTE_HOME) {
        HomeRoute(
            openAddTextScreen = openAddTextScreen,
            openTextsScreen = openTextsScreen,
            openMusicAndAudioScreen = openMusicAndAudioScreen,
            openMediaScreen = openMediaScreen,
            modifier = Modifier.fillMaxSize(),
            sharedDataViewModel = sharedDataViewModel,
        )
    }
}