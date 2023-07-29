package ir.mehdiyari.krypt.backup

internal sealed class BackupViewState {
    object Started : BackupViewState()
    data class Failed(val reasonCode: Int) : BackupViewState()
    object Finished : BackupViewState()
    object Canceled : BackupViewState()
}