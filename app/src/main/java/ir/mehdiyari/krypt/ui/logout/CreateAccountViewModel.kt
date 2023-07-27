package ir.mehdiyari.krypt.ui.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.account.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.account.exeptions.BadAccountNameThrowable
import ir.mehdiyari.krypt.account.exeptions.PasswordLengthThrowable
import ir.mehdiyari.krypt.account.exeptions.PasswordsNotMatchThrowable
import ir.mehdiyari.krypt.dispatchers.di.DispatchersQualifierType
import ir.mehdiyari.krypt.dispatchers.di.DispatchersType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    @DispatchersType(DispatchersQualifierType.IO) private val ioDispatcher: CoroutineDispatcher
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
                        is PasswordsNotMatchThrowable -> ir.mehdiyari.krypt.shared.designsystem.resources.R.string.password_not_match
                        else -> ir.mehdiyari.krypt.shared.designsystem.resources.R.string.something_went_wrong
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