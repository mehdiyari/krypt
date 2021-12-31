package ir.mehdiyari.krypt.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val LightColors = lightColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF03DAC5),
    secondaryVariant = Color(0xFF018786),
    onSecondary = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    error = Color(0xFFB00020)
)

val DarkColors = darkColors(
    primary = Color(0xFFBB86FC),
    primaryVariant = Color(0xFF3700B3),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFF03DAC5),
    secondaryVariant = Color(0xFF03DAC5),
    onSecondary = Color(0xFF000000),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFFFFFFF),
    error = Color(0xFFCF6679)
)

@Composable
fun KryptTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colors = if (darkTheme) DarkColors else LightColors, content = content)
}