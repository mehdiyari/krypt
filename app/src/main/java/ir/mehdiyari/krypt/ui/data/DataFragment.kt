package ir.mehdiyari.krypt.ui.data

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DataFragment : Fragment() {

    private val viewModel: DataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            DataScreen(viewModel) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.generalMessageFlow.collect {
                if (it != null) {
                    if (it == R.string.saving_backup_permission_error) {
                        showPermissionSnackbar(it)
                    } else {
                        Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun showPermissionSnackbar(it: Int) {
        Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).also { snackBar ->
            snackBar.setAction(
                R.string.grant
            ) {
                try {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts(
                            "package",
                            requireActivity().packageName,
                            null
                        )
                    })
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

                snackBar.dismiss()
            }
        }.show()
    }

    override fun onDestroy() {
        viewModel.cancelBackup()
        super.onDestroy()
    }

}