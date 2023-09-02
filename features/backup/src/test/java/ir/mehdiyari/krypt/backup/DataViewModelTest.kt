package ir.mehdiyari.krypt.backup

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import ir.mehdiyari.krypt.backup.logic.backup.BackupRepository
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import ir.mehdiyari.krypt.files.logic.utils.MediaStoreManager
import ir.mehdiyari.krypt.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DataViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val backupRepository = mockk<BackupRepository>(relaxed = true)
    private val filesRepository = mockk<FilesRepository>(relaxed = true)
    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = UnconfinedTestDispatcher(scheduler)
    private val filesUtilities = mockk<FilesUtilities>(relaxed = true)
    private val mediaStoreManager = mockk<MediaStoreManager>(relaxed = true)

    private val dataViewModel = DataViewModel(
        backupRepository,
        filesRepository,
        dispatcher,
        filesUtilities,
        mediaStoreManager
    )

    @Test
    fun `backupNow - verify work properly`() = runTest(dispatcher) {
        coEvery { backupRepository.backupAll() } returns true
        launch {
            dataViewModel.backupNow()
        }
        dataViewModel.backupViewState.test {
            assertEquals(BackupViewState.Started, awaitItem())
            assertEquals(BackupViewState.Finished, awaitItem())

            awaitComplete()
        }
    }


}
