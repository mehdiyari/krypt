package ir.mehdiyari.krypt.core.designsystem.theme

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val LightColors = lightColorScheme(
    primary = Color(0xFF2196F3),
    primaryContainer = Color(0xFF1976D2),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF4CAF50),
    secondaryContainer = Color(0xFF68B66B),
    onSecondary = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    error = Color(0xFFB00020)
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFBB86FC),
    primaryContainer = Color(0xFF3700B3),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFF03DAC5),
    secondaryContainer = Color(0xFF03DAC5),
    onSecondary = Color(0xFF000000),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFFFFFFF),
    error = Color(0xFFCF6679)
)

@Composable
fun KryptTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}