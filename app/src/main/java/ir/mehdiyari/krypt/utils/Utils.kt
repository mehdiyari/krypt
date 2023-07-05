package ir.mehdiyari.krypt.utils

import android.content.Context
import android.content.res.Configuration

fun getFileProviderAuthority(packageName: String): String = "${packageName}.provider"

fun Context.isInDarkTheme(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

fun formatSize(size: Long): String {
    var internalSize = size
    var suffix: String?
    if (internalSize >= 1024) {
        suffix = "KB"
        internalSize /= 1024
        if (internalSize >= 1024) {
            suffix = "MB"
            internalSize /= 1024
            if (internalSize >= 1024) {
                suffix = "GB"
                internalSize /= 1024

            }
        }
    } else {
        suffix = "Bytes"
    }

    return "$internalSize $suffix"
}

fun Long.convertToReadableTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
    } else {
        String.format("%02d:%02d", minutes, remainingSeconds)
    }
}