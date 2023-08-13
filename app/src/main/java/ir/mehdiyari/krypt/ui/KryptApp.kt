package ir.mehdiyari.krypt.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import ir.mehdiyari.krypt.backup.navigateToData
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.features.auth.create_account.ROUTE_CREATE_ACCOUNT
import ir.mehdiyari.krypt.features.auth.login.ROUTE_LOGIN
import ir.mehdiyari.krypt.features.text.add.navigateToAddText
import ir.mehdiyari.krypt.home.AddBottomSheet
import ir.mehdiyari.krypt.home.AddFab
import ir.mehdiyari.krypt.home.KryptBottomAppBar
import ir.mehdiyari.krypt.home.MainMenuBottomSheet
import ir.mehdiyari.krypt.mediaList.MediaViewAction
import ir.mehdiyari.krypt.mediaList.navigateToMedia
import ir.mehdiyari.krypt.setting.ui.navigateToSettings
import ir.mehdiyari.krypt.shareContent.ShareDataViewModel
import ir.mehdiyari.krypt.ui.navigation.KryptNaveHost
import ir.mehdiyari.krypt.voice.record.record.navigateToAddVoice
import ir.mehdiyari.krypt.shared.designsystem.resources.R as DesignSystemR

@Composable
fun KryptApp(
    kryptAppState: KryptAppState = rememberKryptAppState(),
    hasAnyAccount: Boolean,
    onLockAppClicked: () -> Unit,
    onStopLocker: () -> Unit,
    sharedDataViewModel: ShareDataViewModel,
) {
    KryptTheme {
        val snackbarHostState = remember { SnackbarHostState() }

        var openAddItem by remember { mutableStateOf(false) }
        var openMenu by remember { mutableStateOf(false) }

        if (openAddItem) {
            AddBottomSheet(scope = kryptAppState.coroutineScope, onSelectAddItemMenuItem = {
                when (it) {
                    DesignSystemR.string.add_media -> {
                        kryptAppState.navController.navigateToMedia(
                            MediaViewAction.PICK_MEDIA
                        )
                    }

                    DesignSystemR.string.add_audio -> {
                        kryptAppState.navController.navigateToAddVoice()
                    }

                    DesignSystemR.string.add_text -> {
                        kryptAppState.navController.navigateToAddText()
                    }
                }
            }, dismissBottomSheet = {
                openAddItem = false
            })
        }

        if (openMenu) {
            MainMenuBottomSheet(scope = kryptAppState.coroutineScope, onSelectMainMenuItem = {
                when (it) {
                    DesignSystemR.string.menu_data_usage -> kryptAppState.navController.navigateToData()
                    DesignSystemR.string.menu_settings -> kryptAppState.navController.navigateToSettings()
                }
            }, dismissBottomSheet = { openMenu = false }, R.string.app_name)
        }

        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (kryptAppState.isInHomeRoute) {
                    KryptBottomAppBar(openMenuSheet = {
                        openMenu = true
                    }, onLockClicked = {
                        onLockAppClicked()
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
                KryptNaveHost(
                    kryptAppState = kryptAppState,
                    onStopLocker = onStopLocker,
                    startDestination = if (hasAnyAccount) ROUTE_LOGIN else ROUTE_CREATE_ACCOUNT,
                    sharedDataViewModel = sharedDataViewModel,
                    onRestartApp = onLockAppClicked,
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
}