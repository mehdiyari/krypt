package ir.mehdiyari.krypt.ui.splash

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val accountRepository = mockk<AccountsRepository>(relaxed = true)
    private val splashDelay = 1L

    @Test
    fun `after 1 second delay if the user already signed in - then isAnyAccountsExists_value must be true`() =
        runTest {
            val (collector, collectorJob) = mockIsAccountExistsAndReturnCollector(
                isExist = true
            )

            launch {
                delay(splashDelay + 5)
                coVerify(exactly = 1) {
                    collector.emit(true)
                }
                collectorJob.cancel()
            }
        }

    @Test
    fun `after 1 second delay if the user did not sign in before - then isAnyAccountsExists_value must be false`() =
        runTest {
            val (collector, collectorJob) = mockIsAccountExistsAndReturnCollector(
                isExist = false
            )

            launch {
                delay(splashDelay + 5)
                coVerify(exactly = 1) {
                    collector.emit(false)
                }
                collectorJob.cancel()
            }
        }

    private fun TestScope.mockIsAccountExistsAndReturnCollector(isExist: Boolean): Pair<FlowCollector<Boolean>, Job> {
        coEvery { accountRepository.isAccountExists() } returns isExist
        val dis = UnconfinedTestDispatcher(testScheduler)
        val splashViewModel = SplashViewModel(
            accountRepository,
            dis,
            splashDelay
        )

        val collector = mockk<FlowCollector<Boolean>>(relaxed = true)
        val collectorJob = launch {
            splashViewModel.isAnyAccountsExists.collect(collector)
        }
        return collector to collectorJob
    }
}