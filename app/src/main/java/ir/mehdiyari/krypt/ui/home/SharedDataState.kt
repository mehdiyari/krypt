package ir.mehdiyari.krypt.ui.home

import android.net.Uri

sealed class SharedDataState {
    data class SharedText(val text: String) : SharedDataState()
    data class SharedImages(val images: List<Uri>) : SharedDataState()
}