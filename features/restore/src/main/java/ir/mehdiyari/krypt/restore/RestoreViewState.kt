package ir.mehdiyari.krypt.restore

internal sealed class RestoreViewState {
    object OpenBackupFile : RestoreViewState()
    data class ReadyForRestoreState(
        val filePath: String,
        val isExternalStoragePermissionGranted: Boolean,
    ) : RestoreViewState()

    object Close : RestoreViewState()
    object Success : RestoreViewState()
}