package ir.mehdiyari.krypt.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent

const val APP_DOMAIN = "https://mehdiyari.ir"

fun Context.openBrowser(url: Uri) {
    try {
        CustomTabsIntent.Builder(null)
            .setShowTitle(true)
            .setColorScheme(if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) CustomTabsIntent.COLOR_SCHEME_DARK else CustomTabsIntent.COLOR_SCHEME_LIGHT)
            .setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
            .setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
            .addDefaultShareMenuItem()
            .build().also {
                it.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                it.launchUrl(this, url)
            }
    } catch (t: Throwable) {
        this.openChromeBrowser(url)
    }
}

fun Context.openChromeBrowser(uri: Uri?) {
    Intent().apply {
        data = uri
        action = Intent.ACTION_VIEW
        setPackage("com.android.chrome")
        try {
            startActivity(this)
        } catch (t: ActivityNotFoundException) {
            t.printStackTrace()
            setPackage("")
            try {
                startActivity(this)
            } catch (ignored: Throwable) {
                ignored.printStackTrace()
            }
        }
    }
}