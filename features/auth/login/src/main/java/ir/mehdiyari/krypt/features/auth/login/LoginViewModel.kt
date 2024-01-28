package ir.mehdiyari.krypt.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.account.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.dispatchers.di.DispatchersQualifierType
import ir.mehdiyari.krypt.dispatchers.di.DispatchersType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountsRepository: AccountsRepository,
    @DispatchersType(DispatchersQualifierType.IO) private val dispatcherIO: CoroutineDispatcher
) : ViewModel() {

    private val _allAccountsNameState = MutableStateFlow<List<String>>(listOf())
    val allUserNamesState = _allAccountsNameState.asStateFlow()

    private val _closeLoginState = MutableSharedFlow<Boolean>()
    val closeLoginState = _closeLoginState.asSharedFlow()

    private val _loginState = MutableSharedFlow<LoginViewState>()
    val loginState = _loginState.asSharedFlow()

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
                _loginState.emit(LoginViewState.FailureLogin(ir.mehdiyari.krypt.shared.designsystem.resources.R.string.something_went_wrong))
            }
        }
    }
}