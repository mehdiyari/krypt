package ir.mehdiyari.krypt.app

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.KryptApp
import ir.mehdiyari.krypt.ui.home.ShareDataViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity(), AppLockerStopApi {

    private val viewModel: MainViewModel by viewModels()
    private val shareDataViewModel by viewModels<ShareDataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onNewIntent(intent)
        setContent{
            KryptApp()
        }

        lifecycleScope.launch {
            viewModel.automaticLockState.collectLatest {
                if (it) {
                    val currentDestination =
                        findViewById<FragmentContainerView>(R.id.kryptNavigationFragment)
                            ?.findNavController()
                            ?.currentDestination?.id

                    if (!listOf(
//                            R.id.splashFragment,
//                            R.id.loginFragment,
                            R.id.createAccountFragment
                        ).contains(currentDestination)
                    ) {
                        restartApp()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent?.action == Intent.ACTION_SEND) {
            if ("text/plain" == intent.type) {
                shareDataViewModel.handleSharedText(
                    intent.getStringExtra(Intent.EXTRA_TEXT)
                )
            } else if (intent.type?.startsWith("image/") == true || intent.type?.startsWith("video/") == true) {
                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                    shareDataViewModel.handleSharedMedias(it)
                }
            }
        } else if (intent?.action == Intent.ACTION_SEND_MULTIPLE) {
            if (intent.type?.startsWith("image/") == true
                || intent.type?.startsWith("video/") == true || intent.type?.startsWith("*/*") == true
            ) {
                intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let {
                    shareDataViewModel.handleSharedMedias(
                        *it.mapNotNull { image -> image as? Uri }
                            .toTypedArray())
                }
            }
        }
    }

    override fun onStart() {
        viewModel.onStopLocker()
        super.onStart()
    }

    fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        ProcessPhoenix.triggerRebirth(this, intent)
    }

    override fun onStop() {
        viewModel.onStartLocker()
        super.onStop()
    }

    override fun stopAppLockerManually() {
        viewModel.onStopLocker()
    }
}