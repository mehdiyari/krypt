package ir.mehdiyari.krypt.ui.splash

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val accountRepository = mockk<AccountsRepository>(relaxed = true)
    private val splashDelay = 1L
    private val splashViewModel = SplashViewModel(
        accountRepository,
        splashDelay
    )

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    @Ignore("TODO: MHD")
    fun `after 1 second delay if the user already signed in - then isAnyAccountsExists_value must be true`() =
        runTest {
            val (collector, collectorJob) = mockIsAccountExistsAndReturnCollector(
                isExist = true
            )

            launch {
                delay(splashDelay + 5)
                coVerify(exactly = 1) {
                    collector.emit(SplashScreenUiState.Success(true))
                }
                collectorJob.cancel()
            }
        }

    @Test
    @Ignore("TODO: MHD")
    fun `after 1 second delay if the user did not sign in before - then isAnyAccountsExists_value must be false`() =
        runTest {
            val (collector, collectorJob) = mockIsAccountExistsAndReturnCollector(
                isExist = false
            )

            launch {
                delay(splashDelay + 5)
                coVerify(exactly = 1) {
                    collector.emit(SplashScreenUiState.Success(false))
                }
                collectorJob.cancel()
            }
        }

    private fun TestScope.mockIsAccountExistsAndReturnCollector(isExist: Boolean): Pair<FlowCollector<SplashScreenUiState>, Job> {
        coEvery { accountRepository.isAccountExists() } returns isExist

        val collector = mockk<FlowCollector<SplashScreenUiState?>>(relaxed = true)
        val collectorJob = launch {
            splashViewModel.splashUiState.collect(collector)
        }
        return collector to collectorJob
    }
}