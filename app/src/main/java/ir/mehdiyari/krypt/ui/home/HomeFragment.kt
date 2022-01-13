package ir.mehdiyari.krypt.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
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
        TODO()
    }

    private fun addItemMenuSelected(item: Int) {
        TODO()
    }

    private fun clickOnLockItem() {
        TODO()
    }

    private fun onClickOnHomeCards(fileTypeEnum: FileTypeEnum) {
        TODO()
    }
}