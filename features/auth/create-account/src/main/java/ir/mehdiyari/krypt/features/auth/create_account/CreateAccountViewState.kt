package ir.mehdiyari.krypt.features.auth.create_account

import androidx.annotation.StringRes

internal sealed class CreateAccountViewState {
    object SuccessCreateAccount : CreateAccountViewState()
    data class FailureCreateAccount(@StringRes val errorResId: Int) : CreateAccountViewState()
}