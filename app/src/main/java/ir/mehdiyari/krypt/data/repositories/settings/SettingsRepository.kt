package ir.mehdiyari.krypt.data.repositories.settings

import ir.mehdiyari.krypt.ui.settings.AutoLockItemsEnum

interface SettingsRepository {
    fun storeLockAutomatically(autoLockItemsEnum: AutoLockItemsEnum)
    fun getLockAutomaticallyValue(): AutoLockItemsEnum
}