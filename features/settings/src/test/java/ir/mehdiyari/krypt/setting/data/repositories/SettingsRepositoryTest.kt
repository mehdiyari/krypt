package ir.mehdiyari.krypt.setting.data.repositories

import android.content.SharedPreferences
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import ir.mehdiyari.krypt.setting.data.repositories.SettingsRepositoryImpl.Companion.DEFAULT_VALUE
import ir.mehdiyari.krypt.setting.ui.AutoLockItemsEnum
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class SettingsRepositoryTest {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var settingsRepository: SettingsRepository

    @Before
    fun setup() {
        sharedPreferences = mockk(relaxed = true)
        settingsRepository = SettingsRepositoryImpl(sharedPreferences)
    }

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `when key exists then return correct value in getLockAutomaticallyValue function`() {
        val expected = AutoLockItemsEnum.TwoMinute
        every {
            sharedPreferences.getInt(SettingsRepositoryImpl.AUTO_LOCK_SHARED_PREF_KEY, any())
        } returns expected.ordinal

        val actual = settingsRepository.getLockAutomaticallyValue()

        assertEquals(expected, actual)
    }

    @Test
    fun `when key not exists then return Default value in getLockAutomaticallyValue function`() {
        val expected = AutoLockItemsEnum.Disabled
        every {
            sharedPreferences.getInt(SettingsRepositoryImpl.AUTO_LOCK_SHARED_PREF_KEY, any())
        } returns DEFAULT_VALUE

        val actual = settingsRepository.getLockAutomaticallyValue()

        assertEquals(expected, actual)
    }
}