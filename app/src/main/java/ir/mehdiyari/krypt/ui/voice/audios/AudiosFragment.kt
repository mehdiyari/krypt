package ir.mehdiyari.krypt.ui.voice.audios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.ui.voice.player.MusicPlayerViewModel

@AndroidEntryPoint
class AudiosFragment : Fragment() {

    private val audiosViewModel by viewModels<AudiosViewModel>()
    private val musicPlayerViewModel by viewModels<MusicPlayerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        audiosViewModel.getAudios()
        setContent {
            AudiosScreen(findNavController(), audiosViewModel, musicPlayerViewModel)
        }
    }
}