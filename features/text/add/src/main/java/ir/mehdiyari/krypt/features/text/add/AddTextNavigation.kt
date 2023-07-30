package ir.mehdiyari.krypt.features.text.add

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val ROUTE_ADD_TEXT = "add_text"
private const val KEY_TEXT_Id = "key_text_id"
private const val KEY_SHARED_TEXT = "key_shared_text"


class AddTextArgs(val textId: Long, val sharedText: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        savedStateHandle[KEY_TEXT_Id]!!, savedStateHandle[KEY_SHARED_TEXT]!!
    )
}

fun NavController.navigateToAddText(textId: Long = -1L, sharedText: String = "") {
    this.navigate("$ROUTE_ADD_TEXT?$KEY_TEXT_Id=$textId&$KEY_SHARED_TEXT=$sharedText")
}

fun NavGraphBuilder.addTextScreen(
    onBackPressed: () -> Unit
) {
    composable(
        "$ROUTE_ADD_TEXT?$KEY_TEXT_Id={$KEY_TEXT_Id}&$KEY_SHARED_TEXT={$KEY_SHARED_TEXT}",
        arguments = listOf(
            navArgument(KEY_TEXT_Id) {
                type = NavType.LongType
                defaultValue = -1L
            },
            navArgument(KEY_SHARED_TEXT) {
                type = NavType.StringType
                defaultValue = ""
            },
        )
    ) {
        AddTextRoute(
            modifier = Modifier.fillMaxSize(),
            onBackPressed = onBackPressed,
        )
    }
}

