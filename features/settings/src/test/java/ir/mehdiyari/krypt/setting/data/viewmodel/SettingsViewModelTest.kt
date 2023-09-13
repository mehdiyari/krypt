package ir.mehdiyari.krypt.setting.data.viewmodel

import app.cash.turbine.test
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import ir.mehdiyari.krypt.account.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.setting.data.DeleteAccountHelper
import ir.mehdiyari.krypt.setting.data.repositories.SettingsRepository
import ir.mehdiyari.krypt.setting.data.repositories.SettingsRepositoryImpl
import ir.mehdiyari.krypt.setting.ui.AutoLockItemsEnum
import ir.mehdiyari.krypt.setting.ui.DeleteAccountViewState
import ir.mehdiyari.krypt.setting.ui.SettingsViewModel
import ir.mehdiyari.krypt.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class SettingsViewModelTest {
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var accountRepository: AccountsRepository
    private lateinit var deleteAccountHelper: DeleteAccountHelper
    private val dispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    private lateinit var settingsViewModel: SettingsViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(dispatcher)

    @Before
    fun setup() {
        settingsRepository = mockk<SettingsRepositoryImpl>(relaxed = true)
        accountRepository = mockk<AccountsRepository>(relaxed = true)
        deleteAccountHelper = mockk<DeleteAccountHelper>(relaxed = true)

        settingsViewModel = SettingsViewModel(
            settingsRepository,
            accountRepository,
            deleteAccountHelper,
            dispatcher
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `onSelectAutoLockItem - verify work correctly`() = runTest {
        val autoLockItemsEnum = AutoLockItemsEnum.OneHour
        val collector = mockk<FlowCollector<AutoLockItemsEnum>>(relaxed = true)
        val collectorJob =
            launch { settingsViewModel.automaticallyLockSelectedItem.collect(collector) }
        settingsViewModel.onSelectAutoLockItem(autoLockItemsEnum)

        launch {
            coVerify(exactly = 1) {
                settingsRepository.storeLockAutomatically(autoLockItemsEnum)
                collector.emit(autoLockItemsEnum)
                collectorJob.cancel()
            }
        }
    }

    @Test
    fun `onDeleteCurrentAccount - clear account with valid password`() = runTest {
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
