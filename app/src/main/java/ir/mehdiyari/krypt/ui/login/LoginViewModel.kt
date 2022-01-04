package ir.mehdiyari.krypt.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    accountsRepository: AccountsRepository,
    @DispatcherIO dispatcherIO: CoroutineDispatcher
) : ViewModel() {

    private val _allAccountsNameState = MutableStateFlow<List<String>>(listOf())
    val allAccountsNameState: StateFlow<List<String>> = _allAccountsNameState

    init {
        viewModelScope.launch(dispatcherIO) {
            _allAccountsNameState.emit(accountsRepository.getAllAccountsName())
        }
    }
}