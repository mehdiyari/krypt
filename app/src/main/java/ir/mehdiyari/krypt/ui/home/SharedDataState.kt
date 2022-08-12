package ir.mehdiyari.krypt.ui.home

import android.net.Uri

sealed class SharedDataState {
    data class SharedText(val text: String) : SharedDataState()
    data class SharedMedias(val medias: List<Uri>) : SharedDataState()
}