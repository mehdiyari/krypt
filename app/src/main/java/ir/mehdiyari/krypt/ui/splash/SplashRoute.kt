package ir.mehdiyari.krypt.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme


@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    accountExists: () -> Unit,
    noAccountExists: () -> Unit
) {

    val uiState by viewModel.splashUiState.collectAsStateWithLifecycle()

    if (uiState is SplashScreenUiState.Success) {
        if ((uiState as SplashScreenUiState.Success).isAnyAccountsExists) {
            accountExists()
        } else {
            noAccountExists()
        }
    }

    SplashScreen()

}

@Composable
fun SplashScreen() {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.krypt),
            contentDescription = stringResource(id = R.string.splash_content_description),
        )

        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(4.dp),
            fontSize = 18.sp
        )
    }

}

@Preview
@Composable
fun SplashScreenPreview() {

    KryptTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SplashScreen()
        }
    }

}