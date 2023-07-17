package ir.mehdiyari.krypt.core.designsystem.theme

import android.content.Context
import android.content.res.Configuration

fun Context.isInDarkTheme(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}