package ir.mehdiyari.krypt.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ir.mehdiyari.krypt.ui.navigation.KryptNaveHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KryptApp(
    kryptAppState: KryptAppState = rememberKryptAppState()
) {

    val snackbarHostState = remember { SnackbarHostState() }

    var openAddBottomSheet by remember { mutableStateOf(false) }
    var openMenuBottomSheet by remember { mutableStateOf(false) }
    val addItemsBottomSheetState = rememberModalBottomSheetState()
    val mainMenuBottomSheetState = rememberModalBottomSheetState()

    if (openAddBottomSheet) {
        AddBottomSheet(
            addItemsBottomSheetState,
            onSelectAddItemMenuItem = {
//            when (it) {
//                R.string.menu_data_usage -> navigateToDataUsageFragment()
//                R.string.menu_change_password -> navigateToChangePasswordFragment()
//                R.string.menu_settings -> navigateToSettingsFragment()
//                R.string.menu_help ->         requireContext().openBrowser(Uri.parse(APP_DOMAIN))
//            }
            },
            kryptAppState.coroutineScope
        )
    }

    if (openMenuBottomSheet) {
        MainMenuBottomSheet(
            mainMenuBottomSheetState,
            onSelectMainMenuItem = {
//            when (it) {
//                R.string.add_media -> navigateToMediasFragment(MediaFragmentAction.PICK_MEDIA)
//                R.string.add_audio -> navigateToAudioRecorderFragment()
//                R.string.add_text -> navigateToNewTextFragment()
//            }
            },
            kryptAppState.coroutineScope
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (kryptAppState.isInHomeRoute) {
                KryptBottomAppBar(
                    addItemsBottomSheetState,
                    mainMenuBottomSheetState,
                    kryptAppState.coroutineScope,
                    onLockClicked = {
//                        viewModel.lockKrypt()
//                        (requireActivity() as MainActivity).restartApp()
                    })
            }
        },
        floatingActionButtonPosition = FabPosition.Center, // Currently in Material3 docked FAB is not supported (https://issuetracker.google.com/issues/223757073)
        floatingActionButton = {
            if (kryptAppState.isInHomeRoute) {
                AddFab(
                    addItemsBottomSheetState = addItemsBottomSheetState,
                    mainMenuBottomSheetState = mainMenuBottomSheetState,
                    coroutineScope = kryptAppState.coroutineScope
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            KryptNaveHost(kryptAppState = kryptAppState,
                onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = SnackbarDuration.Short,
                    ) == SnackbarResult.ActionPerformed
                })
        }
    }

}