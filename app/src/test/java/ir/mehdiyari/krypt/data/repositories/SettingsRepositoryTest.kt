package ir.mehdiyari.krypt.data.repositories

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import ir.mehdiyari.krypt.data.repositories.SettingsRepository.Companion.DEFAULT_VALUE
import ir.mehdiyari.krypt.ui.settings.AutoLockItemsEnum
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SettingsRepositoryTest {
    private val sharedPreferences: SharedPreferences = mockk(relaxed = true)
    private val settingsRepository = SettingsRepository(sharedPreferences)

    @Test
    fun `when key exists then return correct value in getLockAutomaticallyValue function`() {
        val expected = AutoLockItemsEnum.TwoMinute
        every {
            sharedPreferences.getInt(SettingsRepository.AUTO_LOCK_SHARED_PREF_KEY, any())
        } returns expected.ordinal

        val actual = settingsRepository.getLockAutomaticallyValue()

        assertEquals(expected, actual)
    }

    @Test
    fun `when key not exists then return Default value in getLockAutomaticallyValue function`() {
        val expected = AutoLockItemsEnum.Disabled
        every {
            sharedPreferences.getInt(SettingsRepository.AUTO_LOCK_SHARED_PREF_KEY, any())
        } returns DEFAULT_VALUE

        val actual = settingsRepository.getLockAutomaticallyValue()

        assertEquals(expected, actual)
    }
}