package ir.mehdiyari.krypt.account.api

import javax.crypto.SecretKey

interface UserKeyProvider {

    fun getKey(): SecretKey?

}