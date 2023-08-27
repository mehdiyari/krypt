package ir.mehdiyari.krypt.setting.data.viewmodel

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.mehdiyari.krypt.account.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.setting.data.DeleteAccountHelper
import ir.mehdiyari.krypt.setting.data.repositories.SettingsRepositoryImpl
import ir.mehdiyari.krypt.setting.ui.AutoLockItemsEnum
import ir.mehdiyari.krypt.setting.ui.DeleteAccountViewState
import ir.mehdiyari.krypt.setting.ui.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val settingsRepository = mockk<SettingsRepositoryImpl>(relaxed = true)
    private val accountRepository = mockk<AccountsRepository>(relaxed = true)
    private val deleteAccountHelper = mockk<DeleteAccountHelper>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private val settingsViewModel: SettingsViewModel by lazy {
        SettingsViewModel(
            settingsRepository,
            accountRepository,
            deleteAccountHelper,
            dispatcher
        )
    }

    @Test
    fun `onSelectAutoLockItem - verify work correctly`() = runTest(dispatcher) {
        val autoLockItemsEnum = AutoLockItemsEnum.OneHour
        val collector = mockk<FlowCollector<AutoLockItemsEnum>>(relaxed = true)
        val collectorJob =
            launch { settingsViewModel.automaticallyLockSelectedItem.collect(collector) }
        settingsViewModel.onSelectAutoLockItem(autoLockItemsEnum)

        coVerify(exactly = 1) {
            settingsRepository.storeLockAutomatically(autoLockItemsEnum)
            collector.emit(autoLockItemsEnum)
            collectorJob.cancel()
        }
    }

    @Test
    fun `onDeleteCurrentAccount - clear account with valid password`() = runTest(dispatcher) {
        coEvery { accountRepository.validatePassword(any()) } returns true
        settingsViewModel.onDeleteCurrentAccount("password")
        settingsViewModel.deleteAccountState.test {
            assertEquals(DeleteAccountViewState.DeleteAccountFinished, awaitItem())
        }
        coVerify(exactly = 1) {
            deleteAccountHelper.clearCurrentAccount()
        }
    }

    @Test
    fun `onDeleteCurrentAccount - emit PasswordsNotMatch with invalid password`() {
        coEvery { accountRepository.validatePassword(any()) } returns false
        settingsViewModel.onDeleteCurrentAccount("password")
        assertEquals(
            DeleteAccountViewState.PasswordsNotMatch,
            settingsViewModel.deleteAccountState.value
        )
    }

    @Test
    fun `onDeleteCurrentAccount - when DeleteAccountViewState throws exception, verify handle exception`() =
        runTest {
            coEvery { accountRepository.validatePassword(any()) } returns true
            coEvery { deleteAccountHelper.clearCurrentAccount() } throws Throwable("something went wrong in clear account")
            settingsViewModel.onDeleteCurrentAccount("password")
            settingsViewModel.deleteAccountState.test {
                assertEquals(DeleteAccountViewState.DeleteAccountFailed, awaitItem())
            }
        }

}