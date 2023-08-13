package ir.mehdiyari.krypt.mediaList

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.mehdiyari.krypt.shareContent.ShareDataViewModel

const val ROUTE_MEDIA = "media"
const val KEY_MEDIA_ACTION = "key_action"

internal class MediaArgs(val action: MediaViewAction) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        action = MediaViewAction.values()
            .first { it.value == savedStateHandle[KEY_MEDIA_ACTION] },
    )
}

fun NavController.navigateToMedia(
    action: MediaViewAction
) {
    this.navigate("$ROUTE_MEDIA/${action.value}")
}

fun NavGraphBuilder.mediaScreen(
    sharedDataViewModel: ShareDataViewModel,
    onBackPressed: () -> Unit,
    onStopLocker: () -> Unit,
) {
    composable(
        "$ROUTE_MEDIA/{$KEY_MEDIA_ACTION}",
        arguments = listOf(
            navArgument(KEY_MEDIA_ACTION) { type = NavType.IntType }
        ),
    ) {
        MediaRoute(
            sharedDataViewModel = sharedDataViewModel,
            modifier = Modifier.fillMaxSize(),
            onBackPressed = onBackPressed,
            onStopLocker = onStopLocker,
        )
    }
}