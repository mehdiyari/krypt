package ir.mehdiyari.krypt.home

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import ir.mehdiyari.krypt.account.api.CurrentUserManager
import ir.mehdiyari.krypt.file.data.entity.FileTypeEnum
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import ir.mehdiyari.krypt.shared.designsystem.resources.R as ResourcesR


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
                    ResourcesR.drawable.ic_gallery_50,
                    ResourcesR.string.medias_library,
                    20
                ),
                it.first()
            )

            // media
            Assert.assertEquals(
                HomeCardsModel(
                    R.drawable.ic_editor_50,
                    ResourcesR.string.texts_library,
                    100
                ),
                it[1]
            )

            // audios
            Assert.assertEquals(
                HomeCardsModel(
                    ResourcesR.drawable.ic_add_audio_24,
                    ResourcesR.string.audios_library,
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