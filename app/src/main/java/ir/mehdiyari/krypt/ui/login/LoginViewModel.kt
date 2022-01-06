package ir.mehdiyari.krypt.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    @DispatcherIO private val dispatcherIO: CoroutineDispatcher
) : ViewModel() {

    private val _allAccountsNameState = MutableStateFlow<List<String>>(listOf())
    val allAccountsNameState: StateFlow<List<String>> = _allAccountsNameState

    private val _closeLoginState = MutableSharedFlow<Boolean>()
    val closeLoginState: SharedFlow<Boolean> = _closeLoginState

    private val _loginState = MutableSharedFlow<LoginViewState>()
    val loginState: SharedFlow<LoginViewState> = _loginState

    init {
        viewModelScope.launch(dispatcherIO) {
            accountsRepository.getAllAccountsName().also {
                if (it.isEmpty()) {
                    _closeLoginState.emit(true)
                } else {
                    _allAccountsNameState.emit(it)
                }
            }
        }
    }

    fun login(
        accountName: String,
        password: String
    ) {
        viewModelScope.launch(dispatcherIO) {
            try {
                if (accountsRepository.login(accountName, password)) {
                    _loginState.emit(LoginViewState.SuccessfulLogin)
                } else {
                    _loginState.emit(LoginViewState.FailureLogin(R.string.name_password_invalid))
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                _loginState.emit(LoginViewState.FailureLogin(R.string.something_went_wrong))
            }
        }
    }
}