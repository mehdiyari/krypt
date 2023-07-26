package ir.mehdiyari.krypt.setting.ui

internal sealed class DeleteAccountViewState {
    object DeleteAccountStarts : DeleteAccountViewState()
    object DeleteAccountFailed : DeleteAccountViewState()
    object DeleteAccountFinished : DeleteAccountViewState()
    object PasswordsNotMatch : DeleteAccountViewState()
}