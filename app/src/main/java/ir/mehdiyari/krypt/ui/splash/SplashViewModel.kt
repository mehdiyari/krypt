package ir.mehdiyari.krypt.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.ui.splash.SplashScreenUiState.*
import ir.mehdiyari.krypt.ui.splash.di.SplashDelay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    accountsRepository: AccountsRepository,
    @SplashDelay splashDelay: Long
) : ViewModel() {

    val splashUiState = flow {
        delay(splashDelay)
        emit(Success(accountsRepository.isAccountExists()))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Loading)
}

sealed interface SplashScreenUiState {
    object Loading : SplashScreenUiState

    data class Success(val isAnyAccountsExists: Boolean) : SplashScreenUiState
}