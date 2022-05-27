package ir.mehdiyari.krypt.ui.text.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddTextFragment : Fragment() {

    private val viewModel: AddTextViewModel by viewModels()
    private val args: AddTextFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        viewModel.handleInputTextID(args.textId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.argsTextViewState.collect {
                handleArgsViewState(it)
            }
        }
        setContent {
            AddTextComposeView(viewModel, args.sharedText) {
                findNavController().popBackStack()
            }
        }
    }

    private fun handleArgsViewState(argsViewState: AddTextArgsViewState?) {
        if (argsViewState != null) {
            if (argsViewState is AddTextArgsViewState.Error) {
                Toast.makeText(context, argsViewState.errorResId, Toast.LENGTH_LONG)
                    .show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveNoteState.collect {
                if (it == true) {
                    Toast.makeText(context, R.string.successfully_encrypt_note, Toast.LENGTH_LONG)
                        .show()
                    findNavController().popBackStack()
                } else if (it == false) {
                    Toast.makeText(context, R.string.failed_to_encrypt_note, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteNoteState.collect {
                if (it == true) {
                    findNavController().popBackStack()
                    Toast.makeText(
                        context,
                        R.string.delete_text_was_successfully,
                        Toast.LENGTH_LONG
                    ).show()
                } else if (it == false) {
                    Toast.makeText(
                        context,
                        R.string.delete_text_was_unsuccessfully,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveNoteValidation.collect {
                if (it != null) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}