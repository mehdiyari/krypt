package ir.mehdiyari.krypt.ui.home

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.app.user.CurrentUserManager
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val fileRepository = mockk<FilesRepository>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())
    private val currentUserManager = mockk<CurrentUserManager>(relaxed = true)

    private val homeViewModel = HomeViewModel(
        fileRepository,
        dispatcher,
        currentUserManager,
    )

    @Test
    fun `verify media count mapper works properly`() = runTest(dispatcher) {
        coEvery { fileRepository.getAllFilesTypeCounts() } returns listOf(
            FileTypeEnum.Photo to 10,
            FileTypeEnum.Video to 10,
            FileTypeEnum.Text to 100,
            FileTypeEnum.Audio to 0,
        )

        homeViewModel.getHomeItems()
        homeViewModel.filesCounts.value.also {
            // media
            Assert.assertEquals(
                HomeCardsModel(
                    R.drawable.ic_gallery_50,
                    R.string.medias_library,
                    20
                ),
                it.first()
            )

            // media
            Assert.assertEquals(
                HomeCardsModel(
                    R.drawable.ic_editor_50,
                    R.string.texts_library,
                    100
                ),
                it[1]
            )

            // audios
            Assert.assertEquals(
                HomeCardsModel(
                    R.drawable.ic_add_audio_24,
                    R.string.audios_library,
                    0
                ),
                it.last()
            )
        }
    }

    @Test
    fun `when automatically locker call lockKrypt - then verify clean function of currentUser called`() =
        runTest(dispatcher) {
            homeViewModel.lockKrypt()
            verify(exactly = 1) { currentUserManager.clearCurrentUser() }
        }

}