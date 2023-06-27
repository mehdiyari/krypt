package ir.mehdiyari.krypt.ui.settings

import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    modifier: Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigationClickIcon: () -> Unit = {}
) {
    val automaticallyLockAppSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val deleteDialogState = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.menu_settings), fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigationClickIcon()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "")
                    }
                }
            )
        }
    ) {
        val deleteAccountViewState = viewModel.deleteAccountState.collectAsStateWithLifecycle()
        when (deleteAccountViewState.value) {
            DeleteAccountViewState.DeleteAccountFailed -> {
                deleteDialogState.value = false
                Toast.makeText(
                    LocalContext.current,
                    R.string.account_delete_error,
                    Toast.LENGTH_LONG
                ).show()
            }

            DeleteAccountViewState.DeleteAccountFinished -> {
                deleteDialogState.value = false
                Toast.makeText(
                    LocalContext.current,
                    R.string.your_account_deleted,
                    Toast.LENGTH_LONG
                ).show()

                TODO("restart the app")
            }

            DeleteAccountViewState.DeleteAccountStarts -> {
                deleteDialogState.value = false
                CircularProgressIndicator(modifier = modifier.size(80.dp))
            }

            null, DeleteAccountViewState.PasswordsNotMatch -> {
                if (deleteAccountViewState.value != null) {
                    Toast.makeText(
                        LocalContext.current,
                        R.string.password_not_match,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                SettingsScreenContent(
                    modifier,
                    viewModel,
                    deleteDialogState,
                    automaticallyLockAppSheetState,
                    scope,
                    topPadding = it.calculateTopPadding(),
                )
            }
        }
    }

    AutomaticallyLockModalBottomSheet(
        modifier = modifier,
        automaticallyLockAppSheetState,
        viewModel.automaticallyLockSelectedItem,
        scope
    ) {
        viewModel.onSelectAutoLockItem(it)
    }
}