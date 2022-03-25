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
                })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTextFiles()
    }
}