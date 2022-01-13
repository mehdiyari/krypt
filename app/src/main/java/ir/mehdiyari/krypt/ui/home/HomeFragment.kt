package ir.mehdiyari.krypt.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.data.file.FileTypeEnum

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            HomeComposeScreen(
                viewModel = viewModel,
                onSelectMainMenuItem = ::mainMenuItemSelected,
                onSelectAddItemMenuItem = ::addItemMenuSelected,
                clickOnLockItem = ::clickOnLockItem,
                clickOnCards = ::onClickOnHomeCards
            )
        }
    }

    private fun mainMenuItemSelected(item: Int) {
        when (item) {
            R.string.menu_data_usage -> navigateToDataUsageFragment()
            R.string.menu_change_password -> navigateToChangePasswordFragment()
            R.string.menu_customize -> navigateToCustomizeFragment()
            R.string.menu_help -> navigateToHelpFragment()
        }
    }

    private fun addItemMenuSelected(item: Int) {
        when (item) {
            R.string.add_photo -> openPhotoPicker()
            R.string.add_video -> openVideoPicker()
            R.string.add_audio -> navigateToAudioRecorderFragment()
            R.string.add_music -> openAudioPicker()
            R.string.add_text -> navigateToNewTextFragment()
        }
    }

    private fun onClickOnHomeCards(fileTypeEnum: FileTypeEnum) {
        when (fileTypeEnum) {
            FileTypeEnum.Photo -> openFalleryWithCustomPhotoGallery()
            FileTypeEnum.Video -> openFalleryWithCustomVideoGallery()
            FileTypeEnum.Audio, FileTypeEnum.Music -> navigateToMusicAndAudioFragment()
            FileTypeEnum.Text -> navigateToTextsFragment()
        }
    }

    private fun clickOnLockItem() {
        TODO()
    }

    private fun openPhotoPicker() {
        TODO("Not yet implemented")
    }

    private fun openVideoPicker() {
        TODO("Not yet implemented")
    }

    private fun navigateToNewTextFragment() {
        TODO("Not yet implemented")
    }

    private fun openAudioPicker() {
        TODO("Not yet implemented")
    }

    private fun navigateToAudioRecorderFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToHelpFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToCustomizeFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToChangePasswordFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToDataUsageFragment() {
        TODO("Not yet implemented")
    }

    private fun openFalleryWithCustomPhotoGallery() {
        TODO("Not yet implemented")
    }

    private fun openFalleryWithCustomVideoGallery() {
        TODO("Not yet implemented")
    }

    private fun navigateToTextsFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToMusicAndAudioFragment() {
        TODO("Not yet implemented")
    }
}