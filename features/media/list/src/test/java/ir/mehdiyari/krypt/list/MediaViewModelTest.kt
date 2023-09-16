package ir.mehdiyari.krypt.list

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.unmockkAll
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import ir.mehdiyari.krypt.files.logic.utils.MediaStoreManager
import ir.mehdiyari.krypt.mediaList.MediaViewAction
import ir.mehdiyari.krypt.mediaList.MediasViewModel
import ir.mehdiyari.krypt.mediaList.SelectedMediaItems
import ir.mehdiyari.krypt.mediaList.data.FalleryBuilderProvider
import ir.mehdiyari.krypt.mediaList.utils.ThumbsUtils
import ir.mehdiyari.krypt.testing.MainDispatcherRule
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

@OptIn(ExperimentalCoroutinesApi::class)
class MediaViewModelTest {
    private lateinit var kryptCryptographyHelper :KryptCryptographyHelper
    private lateinit var filesUtilities : FilesUtilities
    private lateinit var filesRepository : FilesRepository
    private lateinit var mediaStoreManager : MediaStoreManager
    private lateinit var thumbsUtils : ThumbsUtils
    private lateinit var falleryBuilderProvider : FalleryBuilderProvider
    private lateinit var mediasViewModel : MediasViewModel

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = UnconfinedTestDispatcher(scheduler)
    private val savedStateHandle: SavedStateHandle
        get() {
            return SavedStateHandle().apply {
                this["key_action"] = MediaViewAction.PICK_MEDIA.value
            }
        }


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(dispatcher)

    @Before
    fun setup(){
        kryptCryptographyHelper = mockk(relaxed = true)
        filesUtilities = mockk(relaxed = true)
        filesRepository = mockk(relaxed = true)
        mediaStoreManager = mockk(relaxed = true)
        thumbsUtils = mockk(relaxed = true)
        falleryBuilderProvider = mockk(relaxed = true)

        mediasViewModel = MediasViewModel(
            savedStateHandle,
            dispatcher,
            kryptCryptographyHelper,
            filesUtilities,
            filesRepository,
            mediaStoreManager,
            thumbsUtils,
            falleryBuilderProvider,
        )
    }

    @After
    fun tearDown(){
        clearAllMocks()
        unmockkAll()
    }

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


