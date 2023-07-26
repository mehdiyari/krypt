package ir.mehdiyari.krypt.setting.data.repositories

import ir.mehdiyari.krypt.setting.ui.AutoLockItemsEnum

interface SettingsRepository {
    fun storeLockAutomatically(autoLockItemsEnum: AutoLockItemsEnum)
    fun getLockAutomaticallyValue(): AutoLockItemsEnum
}