package ir.mehdiyari.krypt.ui.media

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import ir.mehdiyari.fallery.main.fallery.*
import ir.mehdiyari.fallery.models.BucketType
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.DeviceGalleryImageLoader
import ir.mehdiyari.krypt.utils.getFileProviderAuthority
import ir.mehdiyari.krypt.utils.isInDarkTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaFragment : Fragment() {

    private val viewModel: MediasViewModel by viewModels()
    private val args: MediaFragmentArgs by navArgs()

    @field:Inject
    lateinit var deviceGalleryImageLoader: DeviceGalleryImageLoader

    @field:Inject
    lateinit var encryptedMediasBucketProvider: EncryptedMediasBucketProvider

    @field:Inject
    lateinit var encryptedMediasBucketContentProvider: EncryptedMediasBucketContentProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        viewModel.onActionReceived(args.action)
        setContent {
            MediasComposeScreen(viewModel) {
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.viewAction.collect {
                when (it) {
                    MediaFragmentAction.PICK_MEDIA -> openPhotoPicker()
                    MediaFragmentAction.DECRYPT_MEDIA -> {
                        if (viewModel.checkForOpenPickerForDecryptMode()) {
                            openPhotoPickerForDecrypting()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                R.string.no_encrypted_file_found,
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().popBackStack()
                        }
                    }
                    MediaFragmentAction.TAKE_MEDIA -> TODO()
                    MediaFragmentAction.DEFAULT -> TODO()
                }
            }
        }
    }

    private fun getBaseOptionsOfFallery(): FalleryBuilder = FalleryBuilder()
        .setImageLoader(deviceGalleryImageLoader)
        .mediaTypeFiltering(BucketType.VIDEO_PHOTO_BUCKETS)
        .setFalleryToolbarTitleText(R.string.app_name)
        .setMediaCountEnabled(true)
        .setGrantExternalStoragePermission(true)
        .setGrantSharedStoragePermission(true)
        .setMediaObserverEnabled(true)
        .setCaptionEnabledOptions(CaptionEnabledOptions(false))
        .setTheme(if (requireContext().isInDarkTheme()) ir.mehdiyari.fallery.R.style.Fallery_Dracula else ir.mehdiyari.fallery.R.style.Fallery_Light)

    private fun openPhotoPickerForDecrypting() {
        getBaseOptionsOfFallery()
            .setContentProviders(
                encryptedMediasBucketContentProvider,
                encryptedMediasBucketProvider
            )
            .build().also { options ->
                startFalleryWithOptions(2, options)
            }
    }

    private fun openPhotoPicker() {
        getBaseOptionsOfFallery().setCameraEnabledOptions(
            CameraEnabledOptions(
                true,
                getFileProviderAuthority(requireActivity().application.packageName)
            )
        ).build().also { options ->
            startFalleryWithOptions(1, options)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == RESULT_OK) {
                handleSelectedPhotos(data?.getFalleryResultMediasFromIntent())
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun handleSelectedPhotos(result: Array<String>?) {
        if (result.isNullOrEmpty()) {
            findNavController().popBackStack()
        } else {
            viewModel.onSelectedMedias(result)
        }
    }
}