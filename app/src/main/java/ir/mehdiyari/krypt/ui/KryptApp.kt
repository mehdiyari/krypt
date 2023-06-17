package ir.mehdiyari.krypt.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.ui.login.ROUTE_LOGIN
import ir.mehdiyari.krypt.ui.login.loginScreen
import ir.mehdiyari.krypt.ui.login.navigateToLogin
import ir.mehdiyari.krypt.ui.logout.ROUTE_CREATE_ACCOUNT
import ir.mehdiyari.krypt.ui.navigation.KryptNaveHost
import ir.mehdiyari.krypt.ui.splash.ROUTE_SPLASH
import ir.mehdiyari.krypt.ui.splash.splashScreen
import kotlinx.coroutines.launch

@Composable
fun KryptApp(
    kryptAppState: KryptAppState = rememberKryptAppState()
) {

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (kryptAppState.isInHomeRoute){
                FloatingActionButton(onClick = {
                    scope.launch {
                        if (addItemsBottomSheetState.isVisible) {
                            addItemsBottomSheetState.hide()
                        } else {
                            mainMenuBottomSheetState.hide()
                            addItemsBottomSheetState.show()
                        }
                    }
                }) {
                    Icon(
                        Icons.Filled.Add,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = stringResource(id = R.string.add_items)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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