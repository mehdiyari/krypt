package ir.mehdiyari.krypt.features.text.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val ROUTE_TEXTS = "texts"

fun NavController.navigateToTexts() {
    this.navigate(ROUTE_TEXTS)
}

fun NavGraphBuilder.textsScreen(
    onTextClick: (id: Long) -> Unit,
    onNewNoteClick: () -> Unit,
    onBackPressed: () -> Unit,
) {
    composable(ROUTE_TEXTS) {
        TextsRoute(
            onTextClick = onTextClick,
            onNewNoteClick = onNewNoteClick,
            modifier = Modifier.fillMaxSize(),
            onBackPressed = onBackPressed,
        )
    }
}