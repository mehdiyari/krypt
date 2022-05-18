package ir.mehdiyari.krypt.data.repositories

import android.content.SharedPreferences
import ir.mehdiyari.krypt.ui.settings.AutoLockItemsEnum
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val AUTO_LOCK_SHARED_PREF_KEY = "auto_lock_status"
    }

    fun storeLockAutomatically(autoLockItemsEnum: AutoLockItemsEnum) {
        sharedPreferences.edit().putInt(AUTO_LOCK_SHARED_PREF_KEY, autoLockItemsEnum.ordinal)
            .apply()
    }

    fun getLockAutomaticallyValue(): AutoLockItemsEnum {
        val autoLockValue = sharedPreferences.getInt(AUTO_LOCK_SHARED_PREF_KEY, -1)
        return if (autoLockValue == -1) {
            AutoLockItemsEnum.Disabled
        } else {
            AutoLockItemsEnum.values().firstOrNull {
                it.ordinal == autoLockValue
            } ?: AutoLockItemsEnum.Disabled
        }
    }

}