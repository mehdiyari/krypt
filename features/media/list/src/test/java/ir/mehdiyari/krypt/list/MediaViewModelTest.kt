package ir.mehdiyari.krypt.list

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.mockk
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import ir.mehdiyari.krypt.files.logic.utils.MediaStoreManager
import ir.mehdiyari.krypt.mediaList.MediaViewAction
import ir.mehdiyari.krypt.mediaList.MediasViewModel
import ir.mehdiyari.krypt.mediaList.SelectedMediaItems
import ir.mehdiyari.krypt.mediaList.data.FalleryBuilderProvider
import ir.mehdiyari.krypt.mediaList.utils.ThumbsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class MediaViewModelTest {
    private val savedStateHandle: SavedStateHandle
        get() {
           return SavedStateHandle().apply {
                this["key_action"] = MediaViewAction.PICK_MEDIA.value
            }
        }

    private val kryptCryptographyHelper = mockk<KryptCryptographyHelper>(relaxed = true)
    private val filesUtilities = mockk<FilesUtilities>(relaxed = true)
    private val filesRepository = mockk<FilesRepository>(relaxed = true)
    private val mediaStoreManager = mockk<MediaStoreManager>(relaxed = true)
    private val thumbsUtils = mockk<ThumbsUtils>(relaxed = true)
    private val falleryBuilderProvider = mockk<FalleryBuilderProvider>(relaxed = true)
    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = UnconfinedTestDispatcher(scheduler)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(dispatcher)

    init {
        Dispatchers.setMain(dispatcher)
    }

    private val mediasViewModel = MediasViewModel(
        savedStateHandle,
        dispatcher,
        kryptCryptographyHelper,
        filesUtilities,
        filesRepository,
        mediaStoreManager,
        thumbsUtils,
        falleryBuilderProvider,
    )

    @Test
    fun `emit correct value when a media will be selected`() = runTest(dispatcher) {
        val list: List<String> = listOf("path1", "path2", "path3")

        launch {
            mediasViewModel.onSelectedMedias(list)
        }
        mediasViewModel.selectedMediasFlow.test {
            assertEquals(listOf(
                SelectedMediaItems("path1", false),
                SelectedMediaItems("path2", false),
                SelectedMediaItems("path3", false)), awaitItem())
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
