package ir.mehdiyari.krypt.account.api

interface CurrentUserManager {

    fun setCurrentUser(username: String, key: ByteArray)

    fun clearCurrentUser()

    fun isUserAvailable(): Boolean
}