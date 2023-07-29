package ir.mehdiyari.krypt.app

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.ui.KryptApp
import ir.mehdiyari.krypt.shareContent.ShareDataViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val NAME = "name"
        const val KEY = "key"
    }

    private val viewModel: MainViewModel by viewModels()
    private val shareDataViewModel by viewModels<ir.mehdiyari.krypt.shareContent.ShareDataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var (name, key) = getNameAndKey(savedInstanceState)
        super.onCreate(savedInstanceState)
        viewModel.setNameAndKey(name, key)
        name = null; key = null;

        var splashUiState: SplashUiState by mutableStateOf(SplashUiState.Loading)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.splashUiState.collect{
                    splashUiState = it
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (splashUiState) {
                SplashUiState.Loading -> true
                is SplashUiState.Success -> false
            }
        }

        onNewIntent(intent)
        setContent {
            KryptApp(
                hasAnyAccount = (splashUiState as? SplashUiState.Success)?.isAnyAccountsExists
                    ?: false,
                onLockAppClicked = viewModel::onLockMenuClicked,
                onStopLocker = viewModel::onStopLocker,
                sharedDataViewModel = shareDataViewModel,
            )
        }

        lifecycleScope.launch {
            viewModel.restartAppStateFlow.collectLatest {
                if (it) {
                    restartApp()
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

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        ProcessPhoenix.triggerRebirth(this, intent)
        finish()
    }

    override fun onStop() {
        viewModel.onStartLocker()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        putNameAndKey(outState)
        super.onSaveInstanceState(outState)
    }

    private fun putNameAndKey(outState: Bundle) {
        val currentUser = viewModel.getCurrentUser()
        outState.putString(NAME, currentUser.first)
        outState.putByteArray(KEY, currentUser.second?.encoded)
    }

    private fun getNameAndKey(savedInstanceState: Bundle?): Pair<String?, ByteArray?> =
        savedInstanceState?.let {
            val name = it.getString(NAME)
            val key = it.getByteArray(KEY)
            it.remove(NAME)
            it.remove(KEY)
            return Pair(name, key)
        } ?: (null to null)
}