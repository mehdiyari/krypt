package ir.mehdiyari.krypt.ui.home

sealed class SharedDataState {
    data class SharedText(val text: String) : SharedDataState()
}