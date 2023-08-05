package ir.mehdiyari.krypt.voice.collection

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme
import ir.mehdiyari.krypt.shared.designsystem.resources.R as DesignSystemR


@Composable
internal fun AddNewVoiceButton(
    modifier: Modifier,
    onNavigateToRecordAudio: () -> Unit,
) {
    ExtendedFloatingActionButton(
        modifier = modifier.padding(16.dp),
        onClick = {
            onNavigateToRecordAudio()
        },
        icon = {
            Icon(
                painter = painterResource(id = DesignSystemR.drawable.ic_add_audio_24),
                contentDescription = "",
            )
        },
        text = {
            Text(text = stringResource(id = R.string.add_new_audio))
        }
    )

}


@Composable
@Preview
private fun AddNewVoiceButtonPreview() {
    KryptTheme {
        AddNewVoiceButton(Modifier) {

        }
    }
}