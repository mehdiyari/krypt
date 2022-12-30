package ir.mehdiyari.krypt.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.app.MainActivity
import ir.mehdiyari.krypt.data.file.FileTypeEnum
import ir.mehdiyari.krypt.ui.media.MediaFragmentAction
import ir.mehdiyari.krypt.ui.media.MediaFragmentArgs
import ir.mehdiyari.krypt.ui.media.SharedMediaListModel
import ir.mehdiyari.krypt.ui.text.add.AddTextFragmentArgs
import ir.mehdiyari.krypt.utils.APP_DOMAIN
import ir.mehdiyari.krypt.utils.openBrowser
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
        viewLifecycleOwner.lifecycleScope.launch {
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
            } else if (sharedDataState is SharedDataState.SharedMedias) {
                navigateToMediasFragment(
                    MediaFragmentAction.ENCRYPT_MEDIA,
                    SharedMediaListModel(sharedDataState.medias)
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
        sharedImageList: SharedMediaListModel? = null
    ) {
        try {
            findNavController().navigate(
                R.id.photosFragment,
                MediaFragmentArgs.Builder(sharedImageList)
                    .apply {
                        action = photosAction
                        sharedMedias = sharedImageList
                    }.build().toBundle(),
                navOptions {
                    this.anim {
                        this.enter = android.R.anim.slide_in_left
                        this.exit = android.R.anim.slide_out_right
                    }

                    this.launchSingleTop = true
                },
                null
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            Toast.makeText(requireContext(), R.string.navigation_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToNewTextFragment(text: String? = null) {
        try {
            findNavController().navigate(
                R.id.addTextFragment,
                AddTextFragmentArgs.Builder().apply {
                    sharedText = text
                }.build().toBundle(),
                navOptions {
                    this.anim {
                        this.enter = android.R.anim.slide_in_left
                        this.exit = android.R.anim.slide_out_right
                    }

                    this.launchSingleTop = true
                },
                null
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            Toast.makeText(requireContext(), R.string.navigation_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToAudioRecorderFragment() {
        findNavController().navigate(R.id.action_homeFragment_to_recordVoiceFragment)
    }

    private fun navigateToHelpFragment() {
        requireContext().openBrowser(Uri.parse(APP_DOMAIN))
    }

    private fun navigateToSettingsFragment() {
        findNavController().navigate(R.id.action_home_to_settingsFragment)
    }

    private fun navigateToChangePasswordFragment() {
        //TODO("Not yet implemented")
    }

    private fun navigateToDataUsageFragment() {
        try {
            findNavController().navigate(R.id.action_homeFragment_to_dataFragment)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun navigateToTextsFragment() {
        findNavController().navigate(R.id.action_homeFragment_to_textsFragment)
    }

    private fun navigateToMusicAndAudioFragment() {
        // TODO("Not yet implemented")
    }
}