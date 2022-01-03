package ir.mehdiyari.krypt.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    accountsRepository: AccountsRepository,
    @DispatcherIO ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _isAnyAccountsExists: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isAnyAccountsExists: SharedFlow<Boolean> = _isAnyAccountsExists

    init {
        viewModelScope.launch(ioDispatcher) {
            _isAnyAccountsExists.emit(accountsRepository.isAccountExists())
        }
    }
}