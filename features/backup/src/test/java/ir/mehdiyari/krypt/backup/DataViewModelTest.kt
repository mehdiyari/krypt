package ir.mehdiyari.krypt.backup

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import ir.mehdiyari.krypt.backup.logic.backup.BackupRepository
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import ir.mehdiyari.krypt.files.logic.utils.MediaStoreManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

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
    fun `backupNow - verify work properly whenever getting backup data is successful`() =
        runTest(dispatcher) {
            coEvery { backupRepository.backupAll() } returns true
            launch { dataViewModel.backupNow() }
            dataViewModel.backupViewState.test {
                assertEquals(BackupViewState.Finished, awaitItem())
            }
        }

    @Test
    fun `backupNow - verify work properly whenever getting backup data fail`() =
        runTest(dispatcher) {
            coEvery { backupRepository.backupAll() } returns false
            launch { dataViewModel.backupNow() }
            dataViewModel.backupViewState.test {
                assertEquals(BackupViewState.Failed(0), awaitItem())
            }
        }

    @Test
    fun `backupNow - check cancellation exception handling`() = runTest {
        coEvery { backupRepository.backupAll() } throws CancellationException("Backing up process is cancelled ")
        dataViewModel.backupNow()
        dataViewModel.backupViewState.test {
            assertEquals(BackupViewState.Canceled, awaitItem())
        }
    }

    @Test
    fun `backupNow - check general exception handling`() = runTest {
        coEvery { backupRepository.backupAll() } throws Exception()
        dataViewModel.backupNow()
        dataViewModel.backupViewState.test {
            assertEquals(BackupViewState.Failed(0), awaitItem())
        }
    }

    @Test
    fun `onSaveBackup - check saving backup with backupFileId work properly`() =
        runTest(dispatcher) {
            val backupFileId = 1
            val expectedFilePath = "expectedFilePath"

            val file = File(expectedFilePath)

            coEvery { backupRepository.getBackupFilePathWithId(backupFileId = backupFileId) } returns expectedFilePath
            coEvery { filesUtilities.copyBackupFileToKryptFolder(expectedFilePath) } returns file.path
            launch {
                dataViewModel.onSaveBackup(backupFileId = backupFileId)
                coVerify(exactly = 1) {
                    mediaStoreManager.scanAddedMedia(listOf(expectedFilePath))
                }
            }

            launch {
                dataViewModel.generalMessageFlow.test {
                    assertEquals(
                        "App successfully save backup file into Krypt official folder on your storage. ",
                        awaitItem().toString()
                    )
                }
            }
        }

}

@OptIn(ExperimentalCoroutinesApi::class)
open class MainDispatcherRule constructor(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
