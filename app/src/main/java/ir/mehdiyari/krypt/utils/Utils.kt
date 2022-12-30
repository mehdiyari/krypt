package ir.mehdiyari.krypt.utils

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import ir.mehdiyari.krypt.R

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

fun View.showPermissionSnackbar(@StringRes resId: Int) {
    Snackbar.make(this, resId, Snackbar.LENGTH_LONG).also { snackBar ->
        snackBar.setAction(
            R.string.grant
        ) {
            try {
                this.context.startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts(
                        "package",
                        this@showPermissionSnackbar.context.packageName,
                        null
                    )
                })
            } catch (t: Throwable) {
                t.printStackTrace()
            }

            snackBar.dismiss()
        }
    }.show()
}