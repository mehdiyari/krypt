package ir.mehdiyari.krypt.ui.splash

sealed interface SplashScreenUiState {
    object Loading : SplashScreenUiState
    data class Success(val isAnyAccountsExists: Boolean) : SplashScreenUiState
}