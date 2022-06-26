package ir.mehdiyari.krypt.ui.logout

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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAccountFragment : Fragment() {

    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            CreateAccountComposeScreen(viewModel)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.createAccountViewState.collect {
                when (it) {
                    is CreateAccountViewState.SuccessCreateAccount -> {
                        showSuccessToastMessage()
                        findNavController().navigate(R.id.action_createAccountFragment_to_splashFragment)
                    }
                    is CreateAccountViewState.FailureCreateAccount -> {
                        showErrorWithSnackBar(it.errorResId)
                    }
                }
            }
        }
    }

    private fun showSuccessToastMessage() {
        Toast.makeText(requireContext(), R.string.successfully_create_account, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showErrorWithSnackBar(errorResId: Int) {
        Snackbar.make(requireView(), errorResId, Snackbar.LENGTH_LONG).show()
    }
}