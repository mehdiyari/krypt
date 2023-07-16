package ir.mehdiyari.krypt.data.repositories.settings

import android.content.SharedPreferences
import ir.mehdiyari.krypt.ui.settings.AutoLockItemsEnum
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {
    companion object {
        const val AUTO_LOCK_SHARED_PREF_KEY = "auto_lock_status"
        const val DEFAULT_VALUE = -1
    }

    override fun storeLockAutomatically(autoLockItemsEnum: AutoLockItemsEnum) {
        sharedPreferences.edit().putInt(AUTO_LOCK_SHARED_PREF_KEY, autoLockItemsEnum.ordinal)
            .apply()
    }

    override fun getLockAutomaticallyValue(): AutoLockItemsEnum {
        val autoLockValue = sharedPreferences.getInt(AUTO_LOCK_SHARED_PREF_KEY, DEFAULT_VALUE)
        return if (autoLockValue == DEFAULT_VALUE) {
            AutoLockItemsEnum.Disabled
        } else {
            AutoLockItemsEnum.values().firstOrNull {
                it.ordinal == autoLockValue
            } ?: AutoLockItemsEnum.Disabled
        }
    }

}