package ir.mehdiyari.krypt.ui.logout

import androidx.annotation.StringRes

sealed class CreateAccountViewState {
    object SuccessCreateAccount : CreateAccountViewState()
    data class FailureCreateAccount(@StringRes val errorResId: Int) : CreateAccountViewState()
}