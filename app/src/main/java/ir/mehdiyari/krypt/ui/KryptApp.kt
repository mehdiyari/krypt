package ir.mehdiyari.krypt.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.navigation.KryptNaveHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KryptApp(
    kryptAppState: KryptAppState = rememberKryptAppState()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val addItemsBottomSheetState = rememberModalBottomSheetState()
    val mainMenuBottomSheetState = rememberModalBottomSheetState()

    if (addItemsBottomSheetState.currentValue == SheetValue.Expanded) {
        AddBottomSheet(
            addItemsBottomSheetState,
            onSelectAddItemMenuItem = {
                when (it) {
                    R.string.menu_data_usage -> TODO("navigateToDataUsageFragment")
                    R.string.menu_change_password -> TODO("navigateToChangePasswordFragment")
                    R.string.menu_settings -> TODO("navigateToSettingsFragment")
                    R.string.menu_help -> TODO("openBrowser")
                }
            },
            kryptAppState.coroutineScope
        )
    }

    if (mainMenuBottomSheetState.currentValue == SheetValue.Expanded) {
        MainMenuBottomSheet(
            mainMenuBottomSheetState,
            onSelectMainMenuItem = {
                when (it) {
                    R.string.add_media -> TODO("navigateToMediasFragment")
                    R.string.add_audio -> TODO("navigateToAudioRecorderFragment")
                    R.string.add_text -> TODO("navigateToNewTextFragment")
                }
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
                        TODO("viewModel.lockKrypt(), restartApp")
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