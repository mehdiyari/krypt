package ir.mehdiyari.krypt.ui.text.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.text.add.AddTextFragmentArgs

@AndroidEntryPoint
class TextsFragment : Fragment() {

    private val viewModel: TextsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            TextsComposeView(viewModel = viewModel,
                onNavigationClickIcon = {
                    findNavController().popBackStack()
                }, newNoteClick = {
                    findNavController().navigate(R.id.action_textsFragment_to_addTextFragment)
                }, onCardsClick = { clickedTextId ->
                    findNavController().navigate(
                        resId = R.id.action_textsFragment_to_addTextFragment,
                        args = AddTextFragmentArgs.Builder().apply {
                            textId = clickedTextId
                        }.build().toBundle(),
                        navOptions = null
                    )
                })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTextFiles()
    }
}