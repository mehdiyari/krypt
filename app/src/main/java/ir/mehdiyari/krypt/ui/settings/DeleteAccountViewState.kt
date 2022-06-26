package ir.mehdiyari.krypt.ui.settings

sealed class DeleteAccountViewState {
    object DeleteAccountStarts : DeleteAccountViewState()
    object DeleteAccountFailed : DeleteAccountViewState()
    object DeleteAccountFinished : DeleteAccountViewState()
    object PasswordsNotMatch : DeleteAccountViewState()
}