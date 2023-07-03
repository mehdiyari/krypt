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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.data.navigateToData
import ir.mehdiyari.krypt.ui.media.MediaViewAction
import ir.mehdiyari.krypt.ui.media.navigateToMedia
import ir.mehdiyari.krypt.ui.navigation.KryptNaveHost
import ir.mehdiyari.krypt.ui.settings.navigateToSettings
import ir.mehdiyari.krypt.ui.text.add.navigateToAddText
import ir.mehdiyari.krypt.ui.voice.record.navigateToAddVoice
import ir.mehdiyari.krypt.utils.KryptTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KryptApp(
    kryptAppState: KryptAppState = rememberKryptAppState()
) {
    KryptTheme {
        val snackbarHostState = remember { SnackbarHostState() }

        var openAddItem by remember { mutableStateOf(false) }
        var openMenu by remember { mutableStateOf(false) }

        if (openAddItem) {
            AddBottomSheet(scope = kryptAppState.coroutineScope, onSelectAddItemMenuItem = {
                when (it) {
                    R.string.add_media -> {
                        kryptAppState.navController.navigateToMedia(
                            MediaViewAction.PICK_MEDIA
                        )
                    }

                    R.string.add_audio -> {
                        kryptAppState.navController.navigateToAddVoice()
                    }

                    R.string.add_text -> {
                        kryptAppState.navController.navigateToAddText()
                    }
                }
            }, hideBottomSheet = {
                openAddItem = false
            })
        }

        if (openMenu) {
            MainMenuBottomSheet(scope = kryptAppState.coroutineScope, onSelectMainMenuItem = {
                when (it) {
                    R.string.menu_data_usage -> kryptAppState.navController.navigateToData()
                    R.string.menu_change_password -> TODO("navigateToChangePasswordFragment")
                    R.string.menu_settings -> kryptAppState.navController.navigateToSettings()
                    R.string.menu_help -> TODO("openBrowser")
                }
            }, hideBottomSheet = { openMenu = false })
        }

        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (kryptAppState.isInHomeRoute) {
                    KryptBottomAppBar(openMenuSheet = {
                        openMenu = true
                    }, onLockClicked = {
                        TODO("viewModel.lockKrypt(), restartApp")
                    })
                }
            },
            floatingActionButtonPosition = FabPosition.Center, // Currently in Material3 docked FAB is not supported (https://issuetracker.google.com/issues/223757073)
            floatingActionButton = {
                if (kryptAppState.isInHomeRoute) {
                    AddFab(openAddItemSheet = {
                        openAddItem = true
                    })
                }
            }) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                KryptNaveHost(kryptAppState = kryptAppState, onShowSnackbar = { message, action ->
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = action,
                        duration = SnackbarDuration.Short,
                    ) == SnackbarResult.ActionPerformed
                })
            }
        }
    }
}