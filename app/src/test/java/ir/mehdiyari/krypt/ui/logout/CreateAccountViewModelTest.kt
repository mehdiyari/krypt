package ir.mehdiyari.krypt.ui.logout

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.ui.logout.throwables.BadAccountNameThrowable
import ir.mehdiyari.krypt.ui.logout.throwables.PasswordLengthThrowable
import ir.mehdiyari.krypt.ui.logout.throwables.PasswordsNotMatchThrowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateAccountViewModelTest {

    private val accountsRepository = mockk<AccountsRepository>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())
    private val createAccountViewModel = CreateAccountViewModel(
        accountsRepository,
        dispatcher
    )

    @Test
    fun `User wants to create account - when everything is okay - then state must be SuccessCreateAccount`() =
        runTest(dispatcher) {
            val username = "Richard"
            val password = "JhR4$^*&^kdHJAJan"
            val collector = mockk<FlowCollector<CreateAccountViewState>>(relaxed = true)
            coEvery {
                accountsRepository.addAccount(
                    username,
                    password,
                    password
                )
            } returns (true to null)

            val collectorJob =
                launch { createAccountViewModel.createAccountViewState.collect(collector) }
            createAccountViewModel.addAccount(username, password, password)

            launch {
                coVerify(exactly = 1) {
                    collector.emit(
                        CreateAccountViewState.SuccessCreateAccount
                    )
                }
                collectorJob.cancel()
            }
        }

    @Test
    fun `User wants to create account - when PasswordLength is smaller than 12 - then state must be FailureCreateAccount`() =
        runTest(dispatcher) {
            val username = "Richard"
            val password = "JhR"
            val (collector, collectorJob) = mockErrorStateOfCreateAccount(
                username,
                password,
                password,
                PasswordLengthThrowable()
            )

            createAccountViewModel.addAccount(username, password, password)

            launch {
                coVerify(exactly = 1) {
                    collector.emit(
                        CreateAccountViewState.FailureCreateAccount(R.string.password_length_error)
                    )
                }
                collectorJob.cancel()
            }
        }

    @Test
    fun `User wants to create account - when AccountName has validation issue - then state must be FailureCreateAccount`() =
        runTest(dispatcher) {
            val username = "RM"
            val password = "JhR4$^*&^kdHJAJan"
            val (collector, collectorJob) = mockErrorStateOfCreateAccount(
                username, password, password, BadAccountNameThrowable()
            )

            createAccountViewModel.addAccount(username, password, password)

            launch {
                coVerify(exactly = 1) {
                    collector.emit(
                        CreateAccountViewState.FailureCreateAccount(R.string.account_length_error)
                    )
                }
                collectorJob.cancel()
            }
        }

    @Test
    fun `User wants to create account - when passwords does not match - then state must be FailureCreateAccount`() =
        runTest(dispatcher) {
            val username = "Richard"
            val password = "JhR4$^*&^kdHJAJan"
            val confirmPassword = "JhR4$^*&^kdHJAJan1413254254365"
            val (collector, collectorJob) = mockErrorStateOfCreateAccount(
                username, password, confirmPassword, PasswordsNotMatchThrowable()
            )

            createAccountViewModel.addAccount(username, password, confirmPassword)
            launch {
                coVerify(exactly = 1) {
                    collector.emit(
                        CreateAccountViewState.FailureCreateAccount(R.string.password_not_match)
                    )
                }
                collectorJob.cancel()
            }
        }

    @Test
    fun `User wants to create account - when unknown error happened - then state must be FailureCreateAccount`() =
        runTest(dispatcher) {
            val username = "Richard"
            val password = "JhR4$^*&^kdHJAJan"
            val (collector, collectorJob) = mockErrorStateOfCreateAccount(
                username, password, password, Throwable("Unknown Error")
            )

            createAccountViewModel.addAccount(username, password, password)
            launch {
                coVerify(exactly = 1) {
                    collector.emit(
                        CreateAccountViewState.FailureCreateAccount(R.string.something_went_wrong)
                    )
                }
                collectorJob.cancel()
            }
        }

    private fun TestScope.mockErrorStateOfCreateAccount(
        username: String,
        password: String,
        confirmPassword: String,
        error: Throwable
    ): Pair<FlowCollector<CreateAccountViewState>, Job> {
        val collector = mockk<FlowCollector<CreateAccountViewState>>(relaxed = true)
        coEvery {
            accountsRepository.addAccount(
                username,
                password,
                confirmPassword
            )
        } returns (false to error)

        val collectorJob = launch {
            createAccountViewModel.createAccountViewState.collect(collector)
        }

        return Pair(collector, collectorJob)
    }
}