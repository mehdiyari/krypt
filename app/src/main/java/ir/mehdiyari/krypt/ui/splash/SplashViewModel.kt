package ir.mehdiyari.krypt.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import ir.mehdiyari.krypt.ui.splash.di.SplashDelay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    accountsRepository: AccountsRepository,
    @DispatcherIO ioDispatcher: CoroutineDispatcher,
    @SplashDelay splashDelay: Long
) : ViewModel() {

    private val _isAnyAccountsExists: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isAnyAccountsExists = _isAnyAccountsExists.asSharedFlow()

    init {
        viewModelScope.launch(ioDispatcher) {
            delay(splashDelay)
            _isAnyAccountsExists.emit(accountsRepository.isAccountExists())
        }
    }
}