package ir.mehdiyari.krypt.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

internal data class HomeCardsModel(
    @DrawableRes val icon: Int,
    @StringRes val name: Int,
    val counts: Long
)