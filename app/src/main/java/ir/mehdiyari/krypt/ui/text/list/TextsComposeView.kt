package ir.mehdiyari.krypt.ui.text.list

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ir.mehdiyari.krypt.utils.KryptTheme

@Composable
@Preview
fun TextsComposeView() {
    KryptTheme {
        Text(text = "Texts View")
    }
}