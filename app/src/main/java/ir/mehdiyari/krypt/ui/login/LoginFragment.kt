package ir.mehdiyari.krypt.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            LoginComposeScreen(viewModel) {
                findNavController().popBackStack()
                findNavController().navigate(R.id.createAccountFragment)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.closeLoginState.collect {
                if (it) {
                    findNavController().navigate(R.id.action_loginFragment_to_createAccountFragment)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginState.collect {
                when (it) {
                    is LoginViewState.FailureLogin -> showErrorWithSnackBar(it.errorId)
                    LoginViewState.SuccessfulLogin -> findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
            }
        }
    }

    private fun showErrorWithSnackBar(errorId: Int) {
        Snackbar.make(requireView(), errorId, Snackbar.LENGTH_LONG).show()
    }
}