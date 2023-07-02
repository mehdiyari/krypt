package ir.mehdiyari.krypt.app.user

import javax.crypto.SecretKey

interface UserKeyProvider {

    fun getKey(): SecretKey?

}