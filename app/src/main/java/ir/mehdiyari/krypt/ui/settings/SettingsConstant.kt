package ir.mehdiyari.krypt.ui.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ir.mehdiyari.krypt.R


val SETTINGS_LIST = listOf<Pair<@DrawableRes Int, @StringRes Int>>(
    R.drawable.ic_lock_clock_24 to R.string.settings_lock_auto
)

val AUTO_LOCK_CRYPT_ITEMS = listOf(
    R.string.settings_lock_auto_disabled,
    R.string.settings_lock_auto_30_second,
    R.string.settings_lock_auto_1_min,
    R.string.settings_lock_auto_2_min,
    R.string.settings_lock_auto_5_min,
    R.string.settings_lock_auto_10_min,
    R.string.settings_lock_auto_15_min,
    R.string.settings_lock_auto_30_min,
    R.string.settings_lock_auto_1_hour,
)