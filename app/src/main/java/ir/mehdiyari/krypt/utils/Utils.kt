package ir.mehdiyari.krypt.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment

fun getFileProviderAuthority(packageName: String): String = "${packageName}.provider"

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

fun checkIfAppIsStorageManager(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    Environment.isExternalStorageManager()
} else true

fun Context.requestGrantManagerStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        startActivity(Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
    }
}