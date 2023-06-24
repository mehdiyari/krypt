package ir.mehdiyari.krypt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ir.mehdiyari.krypt.ui.KryptAppState
import ir.mehdiyari.krypt.ui.home.homeScreen
import ir.mehdiyari.krypt.ui.home.navigateToHome
import ir.mehdiyari.krypt.ui.login.loginScreen
import ir.mehdiyari.krypt.ui.login.navigateToLogin
import ir.mehdiyari.krypt.ui.logout.createAccountScreen
import ir.mehdiyari.krypt.ui.logout.navigateToCreateAccount
import ir.mehdiyari.krypt.ui.splash.ROUTE_SPLASH
import ir.mehdiyari.krypt.ui.splash.splashScreen
import ir.mehdiyari.krypt.ui.text.add.addTextScreen
import ir.mehdiyari.krypt.ui.text.add.navigateToAddText
import ir.mehdiyari.krypt.ui.text.list.navigateToTexts
import ir.mehdiyari.krypt.ui.text.list.textsScreen

@Composable
fun KryptNaveHost(
    kryptAppState: KryptAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = ROUTE_SPLASH
) {
    val navController = kryptAppState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        splashScreen(accountExists = {
            navController.navigateToLogin()
        }, noAccountExists = {
            navController.navigateToCreateAccount()
        })

        loginScreen(onCreateAccountClicked = {
            navController.navigateToCreateAccount()
        }, onLoginSuccess = {
            navController.navigateToHome()
        }, showSnackBar = onShowSnackbar)

        createAccountScreen(onCreateAccountSuccess = {
            navController.navigateToLogin()
        }, onShowSnackbar = onShowSnackbar)

        homeScreen(
            openTextsScreen = { navController.navigateToTexts() },
            openMediaScreen = { mediaFragmentAction, sharedMediaListModel -> },
            openMusicAndAudioScreen = {},
            openAudioRecorderScreen = {},
            onShowSnackbar = onShowSnackbar
        )

        textsScreen(onTextClick = {
            navController.navigateToAddText(textId = it)
        }, onNewNoteClick = {
            navController.navigateToAddText()
        }, onBackPressed = {
            navController.popBackStack()
        })

        addTextScreen()

    }
}

