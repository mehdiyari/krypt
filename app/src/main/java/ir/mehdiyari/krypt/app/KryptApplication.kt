package ir.mehdiyari.krypt.app

import android.app.Application
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.HiltAndroidApp
import ir.mehdiyari.krypt.app.user.CurrentUserManager
import ir.mehdiyari.krypt.app.user.UserKeyProvider
import ir.mehdiyari.krypt.app.user.UsernameProvider
import ir.mehdiyari.krypt.data.repositories.CurrentUser
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@HiltAndroidApp
class KryptApplication : Application(), UsernameProvider, UserKeyProvider, CurrentUserManager {

    private val currentUser = CurrentUser(accountName = null, key = null)

    override fun onCreate() {
        super.onCreate()
        if (ProcessPhoenix.isPhoenixProcess(this)) {
            return
        }
    }

    override fun getUsername(): String? = currentUser.accountName

    override fun getKey(): SecretKey? = currentUser.key?.let {
        SecretKeySpec(it, "AES")
    }

    override fun setCurrentUser(username: String, key: ByteArray) {
        currentUser.clear()
        currentUser.accountName = username
        currentUser.key = key
    }

    override fun clearCurrentUser() {
        currentUser.clear()
    }

}