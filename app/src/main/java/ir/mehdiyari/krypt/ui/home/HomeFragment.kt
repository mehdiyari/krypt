package ir.mehdiyari.krypt.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.app.MainActivity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.ui.media.MediaFragmentAction
import ir.mehdiyari.krypt.ui.media.MediaFragmentArgs
import ir.mehdiyari.krypt.ui.media.SharedImagesListModel
import ir.mehdiyari.krypt.ui.text.add.AddTextFragmentArgs
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val shareDataViewModel by activityViewModels<ShareDataViewModel>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHomeData()
        lifecycleScope.launch {
            shareDataViewModel.sharedData.collectLatest {
                if (it != null) {
                    handleSharedDataWithKrypt(it)
                    shareDataViewModel.clearSharedData()
                }
            }
        }
    }

    private fun handleSharedDataWithKrypt(sharedDataState: SharedDataState?) {
        if (sharedDataState != null) {
            if (sharedDataState is SharedDataState.SharedText) {
                navigateToNewTextFragment(sharedDataState.text)
            } else if (sharedDataState is SharedDataState.SharedImages) {
                navigateToMediasFragment(
                    MediaFragmentAction.ENCRYPT_MEDIA,
                    SharedImagesListModel(sharedDataState.images)
                )
            }
        }
    }

    private fun mainMenuItemSelected(item: Int) {
        when (item) {
            R.string.menu_data_usage -> navigateToDataUsageFragment()
            R.string.menu_change_password -> navigateToChangePasswordFragment()
            R.string.menu_settings -> navigateToSettingsFragment()
            R.string.menu_help -> navigateToHelpFragment()
        }
    }

    private fun addItemMenuSelected(item: Int) {
        when (item) {
            R.string.add_media -> navigateToMediasFragment(MediaFragmentAction.PICK_MEDIA)
            R.string.add_audio -> navigateToAudioRecorderFragment()
            R.string.add_text -> navigateToNewTextFragment()
        }
    }

    private fun onClickOnHomeCards(fileTypeEnum: FileTypeEnum) {
        when (fileTypeEnum) {
            FileTypeEnum.Photo -> navigateToMediasFragment(MediaFragmentAction.DECRYPT_MEDIA)
            FileTypeEnum.Audio -> navigateToMusicAndAudioFragment()
            FileTypeEnum.Text -> navigateToTextsFragment()
        }
    }

    private fun clickOnLockItem() {
        viewModel.lockKrypt()
        (requireActivity() as MainActivity).restartApp()
    }

    private fun navigateToMediasFragment(
        photosAction: MediaFragmentAction,
        sharedImageList: SharedImagesListModel? = null
    ) {
        findNavController().navigate(
            R.id.action_homeFragment_to_photosFragment,
            MediaFragmentArgs.Builder().apply {
                action = photosAction
                sharedImages = sharedImageList
            }.build().toBundle(),
            null
        )
    }

    private fun navigateToNewTextFragment(text: String? = null) {
        findNavController().navigate(
            resId = R.id.action_homeFragment_to_addTextFragment,
            args = AddTextFragmentArgs.Builder().apply {
                sharedText = text
            }.build().toBundle(),
            navOptions = null
        )
    }

    private fun navigateToAudioRecorderFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToHelpFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToSettingsFragment() {
        findNavController().navigate(R.id.action_home_to_settingsFragment)
    }

    private fun navigateToChangePasswordFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToDataUsageFragment() {
        TODO("Not yet implemented")
    }

    private fun navigateToTextsFragment() {
        findNavController().navigate(R.id.action_homeFragment_to_textsFragment)
    }

    private fun navigateToMusicAndAudioFragment() {
        TODO("Not yet implemented")
    }
}