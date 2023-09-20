package ir.mehdiyari.krypt.shared.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ir.mehdiyari.krypt.core.designsystem.theme.KryptTheme

@Composable
fun ClickableTextView(
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextUnit = 16.sp,
    onClick: () -> Unit,
) {
    Text(
        modifier = modifier.clickable { onClick() },
        text = text,
        fontSize = textSize,
        fontWeight = FontWeight.Medium,
        style = TextStyle(textDecoration = TextDecoration.Underline),
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Preview
@Composable
private fun ClickableTextViewPreview() {
    KryptTheme {
        ClickableTextView(
            text = "Hey, You can click on this text-view",
        ) {

        }
    }
}