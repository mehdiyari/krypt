package ir.mehdiyari.krypt.ui.text.add

import androidx.annotation.StringRes
import ir.mehdiyari.krypt.ui.text.list.TextEntity

sealed class AddTextArgsViewState {

    data class TextArg(val textEntity: TextEntity) : AddTextArgsViewState()
    data class Error(@StringRes val errorResId: Int) : AddTextArgsViewState()

}