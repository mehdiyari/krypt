package ir.mehdiyari.krypt.voice.collection

import androidx.compose.ui.Modifier
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
        AudiosRoute(
            modifier = Modifier,
            onBackPressed = onBackPressed,
            onNavigateToRecordAudio = onNavigateToRecordAudio
        )
    }
}