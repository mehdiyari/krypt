package ir.mehdiyari.krypt.ui.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HomeCardsModel(
    @DrawableRes val icon: Int,
    @StringRes val name:Int,
    val counts: Long
)