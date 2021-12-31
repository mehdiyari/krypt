package ir.mehdiyari.krypt.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.R
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
@Preview
fun SplashComposeScreen() {
    KryptTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
}