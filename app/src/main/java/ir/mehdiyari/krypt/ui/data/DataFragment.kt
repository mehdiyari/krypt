package ir.mehdiyari.krypt.ui.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onDestroy() {
        viewModel.cancelBackup()
        super.onDestroy()
    }

}