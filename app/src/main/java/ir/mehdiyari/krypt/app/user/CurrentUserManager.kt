package ir.mehdiyari.krypt.app.user

interface CurrentUserManager {

    fun setCurrentUser(username: String, key: ByteArray)

    fun clearCurrentUser()

}