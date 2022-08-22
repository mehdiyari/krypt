package ir.mehdiyari.krypt.ui.logout

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.ui.logout.throwables.BadAccountNameThrowable
import ir.mehdiyari.krypt.ui.logout.throwables.PasswordLengthThrowable
import ir.mehdiyari.krypt.ui.logout.throwables.PasswordsNotMatchThrowable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _createAccountViewState: MutableSharedFlow<CreateAccountViewState> =
        MutableSharedFlow(replay = 0)

    val createAccountViewState = _createAccountViewState
        .asSharedFlow()

    fun addAccount(name: String, password: String, confirmPassword: String) {
        viewModelScope.launch(ioDispatcher) {
            accountsRepository.addAccount(name, password, confirmPassword).also { result ->
                if (result.first) {
                    _createAccountViewState.emit(CreateAccountViewState.SuccessCreateAccount)
                } else {
                    when (result.second) {
                        is PasswordLengthThrowable -> R.string.password_length_error
                        is BadAccountNameThrowable -> R.string.account_length_error
                        is PasswordsNotMatchThrowable -> R.string.password_not_match
                        else -> R.string.something_went_wrong
                    }.also {
                        _createAccountViewState.emit(
                            CreateAccountViewState.FailureCreateAccount(
                                it
                            )
                        )
                    }
                }
            }
        }
    }
}

sealed class CreateAccountViewState {
    object SuccessCreateAccount : CreateAccountViewState()
    data class FailureCreateAccount(@StringRes val errorResId: Int) : CreateAccountViewState()
}