package ir.mehdiyari.krypt.backup

import app.cash.turbine.test
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import ir.mehdiyari.krypt.backup.logic.backup.BackupRepository
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import ir.mehdiyari.krypt.files.logic.utils.MediaStoreManager
import ir.mehdiyari.krypt.testing.MainDispatcherRule
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
internal class DataViewModelTest {

    private lateinit var backupRepository : BackupRepository
    private lateinit var filesRepository : FilesRepository
    private lateinit var filesUtilities : FilesUtilities
    private lateinit var mediaStoreManager : MediaStoreManager
    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = UnconfinedTestDispatcher(scheduler)
    private lateinit var dataViewModel: DataViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(dispatcher)

    @Before
    fun setup(){
        backupRepository = mockk(relaxed = true)
        filesRepository = mockk(relaxed = true)
        filesUtilities = mockk(relaxed = true)
        mediaStoreManager = mockk(relaxed = true)
        dataViewModel = DataViewModel(
            backupRepository,
            filesRepository,
            dispatcher,
            filesUtilities,
            mediaStoreManager
        )
    }

    @After
    fun tearDown(){
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `backupNow - verify work properly whenever getting backup data is successful`() =
        runTest {
            coEvery { backupRepository.backupAll() } returns true
            dataViewModel.backupNow()
            dataViewModel.backupViewState.test {
                assertEquals(BackupViewState.Finished, awaitItem())
            }
        }

    @Test
    fun `backupNow - verify work properly whenever getting backup data fail`() =
        runTest {
            coEvery { backupRepository.backupAll() } returns false
            dataViewModel.backupNow()
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
        runTest {
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
        }

}



