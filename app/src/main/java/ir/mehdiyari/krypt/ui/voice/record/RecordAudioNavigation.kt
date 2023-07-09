package ir.mehdiyari.krypt.ui.voice.record

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ROUTE_ADD_VOICE = "add_voice"

fun NavController.navigateToAddVoice() {
    navigate(ROUTE_ADD_VOICE)
}

fun NavGraphBuilder.addVoiceScreen(
    onBackPressed: () -> Unit
) {
    composable(ROUTE_ADD_VOICE) {
        RecordAudioRoute(modifier = Modifier, onBackPressed = onBackPressed)
    }
}