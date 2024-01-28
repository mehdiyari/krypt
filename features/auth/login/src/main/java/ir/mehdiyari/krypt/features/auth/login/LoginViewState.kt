package ir.mehdiyari.krypt.features.auth.login

import androidx.annotation.StringRes

sealed class LoginViewState {
    object SuccessfulLogin : LoginViewState()
    data class FailureLogin(
        @StringRes val errorId: Int
    ) : LoginViewState()
}