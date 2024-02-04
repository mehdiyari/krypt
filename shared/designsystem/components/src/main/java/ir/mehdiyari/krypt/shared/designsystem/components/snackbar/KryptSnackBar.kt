package ir.mehdiyari.krypt.shared.designsystem.components.snackbar

import androidx.compose.material3.SnackbarDuration

sealed class KryptSnackBar {

    data class Message(
        val message: String,
        val onDismiss: () -> Unit = {},
        val duration: SnackbarDuration,
    ) : KryptSnackBar()

    data class WithAction(
        val message: String,
        val actionText: String,
        val onActionClicked: () -> Unit = {},
        val onDismiss: () -> Unit = {},
        val duration: SnackbarDuration,
    ) : KryptSnackBar()

}