package ir.mehdiyari.krypt.setting.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ir.mehdiyari.krypt.setting.R


internal val SETTINGS_LIST = listOf<Pair<@DrawableRes Int, @StringRes Int>>(
    R.drawable.ic_lock_clock_24 to R.string.settings_lock_auto,
    R.drawable.ic_delete_account_24 to R.string.settings_delete_account_text
)

internal val AUTO_LOCK_CRYPT_ITEMS = listOf(
    AutoLockItemsEnum.Disabled to R.string.settings_lock_auto_disabled,
    AutoLockItemsEnum.ThirtySecond to R.string.settings_lock_auto_30_second,
    AutoLockItemsEnum.OneMinute to R.string.settings_lock_auto_1_min,
    AutoLockItemsEnum.TwoMinute to R.string.settings_lock_auto_2_min,
    AutoLockItemsEnum.FiveMinute to R.string.settings_lock_auto_5_min,
    AutoLockItemsEnum.TenMinute to R.string.settings_lock_auto_10_min,
    AutoLockItemsEnum.FifteenMinute to R.string.settings_lock_auto_15_min,
    AutoLockItemsEnum.ThirtyMinute to R.string.settings_lock_auto_30_min,
    AutoLockItemsEnum.OneHour to R.string.settings_lock_auto_1_hour,
)