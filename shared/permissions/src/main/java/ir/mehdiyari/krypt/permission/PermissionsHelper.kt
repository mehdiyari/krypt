package ir.mehdiyari.krypt.permission

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings

fun checkIfAppIsStorageManager(): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    Environment.isExternalStorageManager()
} else true

fun Context.requestGrantManagerStoragePermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
    }
}

fun getFileProviderAuthority(packageName: String): String = "${packageName}.provider"