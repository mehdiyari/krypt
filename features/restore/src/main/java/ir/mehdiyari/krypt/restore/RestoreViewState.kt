package ir.mehdiyari.krypt.restore

internal sealed class RestoreViewState {
    object OpenBackupFile : RestoreViewState()
    object ReadyForRestoreState : RestoreViewState()
}