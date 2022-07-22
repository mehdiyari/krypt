package ir.mehdiyari.krypt.ui.data

sealed class BackupViewState {
    object Started : BackupViewState()
    data class Failed(val reasonCode: Int) : BackupViewState()
    object Finished : BackupViewState()
    object Canceled : BackupViewState()
}