package ir.mehdiyari.krypt.account.impl

import ir.mehdiyari.krypt.account.api.CurrentUserManager
import ir.mehdiyari.krypt.account.api.UserKeyProvider
import ir.mehdiyari.krypt.account.api.UsernameProvider
import ir.mehdiyari.krypt.account.entity.CurrentUser
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserManagerImpl @Inject constructor() : UsernameProvider, UserKeyProvider,
    CurrentUserManager {

    private val currentUser = CurrentUser(accountName = null, key = null)

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

    override fun isUserAvailable(): Boolean =
        currentUser.accountName != null && currentUser.key != null
}