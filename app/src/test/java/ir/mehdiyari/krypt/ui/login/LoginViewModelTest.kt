package ir.mehdiyari.krypt.ui.login

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val accountsRepository = mockk<AccountsRepository>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())
    private val loginViewModel by lazy(LazyThreadSafetyMode.NONE) {
        LoginViewModel(
            accountsRepository,
            dispatcher
        )
    }

    @Test
    fun `when viewModel initialized and getAllAccountsName returns account list - then verify allAccountsNameState has value`() =
        runTest(dispatcher) {
            val accounts = listOf("Mehdi", "Richard", "Tomas")
            coEvery { accountsRepository.getAllAccountsName() } returns accounts
            loginViewModel.getAccountNames()
            Assert.assertEquals(accounts, loginViewModel.allAccountsNameState.value)
        }

    @Test
    fun `when viewModel initialized and getAllAccountsName returns empty list - then closeLoginState_value must be true`() =
        runTest(dispatcher) {
            coEvery { accountsRepository.getAllAccountsName() } returns listOf()
            val collector = mockk<FlowCollector<Boolean>>(relaxed = true)
            val collectorJob = launch {
                loginViewModel.closeLoginState.collect(collector)
            }

            loginViewModel.getAccountNames()

            launch {
                coVerify(exactly = 1) { collector.emit(true) }
                collectorJob.cancel()
            }
        }

    @Test
    fun `when user tap on login button and user name password is correct - then view state must be SuccessfulLogin`() =
        runTest(dispatcher) {
            val collector = mockk<FlowCollector<LoginViewState>>(relaxed = true)
            val username = "Richard"
            val password = "JhR4$^*&^kdHJAJan"
            coEvery { accountsRepository.login(username, password) } returns true
            val collectorJob = launch { loginViewModel.loginState.collect(collector) }
            loginViewModel.login(username, password)

            launch {
                coVerify(exactly = 1) {
                    collector.emit(LoginViewState.SuccessfulLogin)
                }
                collectorJob.cancel()
            }
        }

    @Test
    fun `when user tap on login button and user name password is wrong - then view state must be FailureLogin`() =
        runTest(dispatcher) {
            val collector = mockk<FlowCollector<LoginViewState>>(relaxed = true)
            val username = "Richard"
            val password = "JhR4$^*&^kdHJAJan"
            coEvery { accountsRepository.login(username, password) } returns false
            val collectorJob = launch { loginViewModel.loginState.collect(collector) }
            loginViewModel.login(username, password)

            launch {
                coVerify(exactly = 1) { collector.emit(LoginViewState.FailureLogin(R.string.name_password_invalid)) }
                collectorJob.cancel()
            }
        }

    @Test
    fun `when user tap on login button login throws an exception - then view state must be FailureLogin with general message`() =
        runTest(dispatcher) {
            val collector = mockk<FlowCollector<LoginViewState>>(relaxed = true)
            val username = "Richard"
            val password = "JhR4$^*&^kdHJAJan"
            coEvery {
                accountsRepository.login(username, password)
            } throws Throwable("something went wrong in validate username and password")

            val collectorJob = launch { loginViewModel.loginState.collect(collector) }
            loginViewModel.login(username, password)

            launch {
                coVerify(exactly = 1) {
                    collector.emit(LoginViewState.FailureLogin(R.string.something_went_wrong))
                }
                collectorJob.cancel()
            }
        }
}