package ir.mehdiyari.krypt.app

sealed interface SplashUiState {
    object Loading : SplashUiState
    data class Success(val isAnyAccountsExists: Boolean) : SplashUiState
}