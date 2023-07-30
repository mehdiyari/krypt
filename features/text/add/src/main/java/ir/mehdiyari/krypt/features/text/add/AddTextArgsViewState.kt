package ir.mehdiyari.krypt.features.text.add

import androidx.annotation.StringRes
import ir.mehdiyari.krypt.features.text.logic.TextEntity

sealed class AddTextArgsViewState {

    data class TextArg(val textEntity: TextEntity) : AddTextArgsViewState()
    data class Error(@StringRes val errorResId: Int) : AddTextArgsViewState()

}