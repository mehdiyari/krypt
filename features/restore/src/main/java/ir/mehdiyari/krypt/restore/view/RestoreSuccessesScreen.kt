package ir.mehdiyari.krypt.restore.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.restore.R

@Composable
internal fun RestoreSuccessesScreen(
    modifier: Modifier,
    onRestart: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(modifier = modifier.align(Alignment.Center)) {

            Image(
                painter = painterResource(R.drawable.ic_restore_successes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(id = R.string.restore_successess_message),
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
                textAlign = TextAlign.Center,
            )

            Button(
                modifier = modifier.align(Alignment.CenterHorizontally),
                onClick = { onRestart() }) {
                Text(text = stringResource(id = R.string.login))
            }
        }

    }
}

@Composable
@Preview
private fun RestoreSuccessesScreenPreview() {
    KryptTheme {
        RestoreSuccessesScreen(
            modifier = Modifier,
            onRestart = {},
        )
    }
}