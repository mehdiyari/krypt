package ir.mehdiyari.krypt.app

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.mehdiyari.krypt.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppLockerStopApi {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            viewModel.automaticLockState.collectLatest {
                if (it) {
                    val currentDestination =
                        findViewById<FragmentContainerView>(R.id.kryptNavigationFragment)
                            ?.findNavController()
                            ?.currentDestination?.id

                    if (!listOf(
                            R.id.splashFragment,
                            R.id.loginFragment,
                            R.id.createAccountFragment
                        ).contains(currentDestination)
                    ) {
                        restartApp()
                    }
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
        startActivity(intent)
        this.finish()
        Runtime.getRuntime().exit(0)
    }

    override fun onStop() {
        viewModel.onStartLocker()
        super.onStop()
    }

    override fun stopAppLockerManually() {
        viewModel.onStopLocker()
    }
}