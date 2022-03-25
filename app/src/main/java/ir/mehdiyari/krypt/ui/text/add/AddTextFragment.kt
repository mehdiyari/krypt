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
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddTextFragment : Fragment() {

    private val viewModel: AddTextViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            AddTextComposeView(viewModel) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
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

        lifecycleScope.launch {
            viewModel.saveNoteValidation.collect {
                if (it != null) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}