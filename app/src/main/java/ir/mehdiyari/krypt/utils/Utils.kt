package ir.mehdiyari.krypt.utils

import android.content.Context
import android.content.res.Configuration

fun getFileProviderAuthority(packageName: String): String = "${packageName}.provider"

fun Context.isInDarkTheme(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}