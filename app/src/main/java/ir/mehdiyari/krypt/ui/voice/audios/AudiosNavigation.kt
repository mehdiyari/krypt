package ir.mehdiyari.krypt.ui.voice.audios

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ROUTE_AUDIOS = "audios"

fun NavController.navigateToAudios() {
    navigate(ROUTE_AUDIOS)
}

fun NavGraphBuilder.audiosRoute(
    onBackPressed: () -> Unit,
    onNavigateToRecordAudio: () -> Unit,
) {
    composable(ROUTE_AUDIOS) {
        AudiosScreen(
            onBackPressed = onBackPressed,
            onNavigateToRecordAudio = onNavigateToRecordAudio
        )
    }
}