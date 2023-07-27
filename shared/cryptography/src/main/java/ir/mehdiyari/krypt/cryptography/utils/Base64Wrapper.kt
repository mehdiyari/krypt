package ir.mehdiyari.krypt.cryptography.utils

import ir.mehdiyari.krypt.cryptography.utils.Base64
import javax.inject.Inject

class Base64Wrapper @Inject constructor() {

    fun decode(base64String: String): ByteArray = Base64.decode(base64String)

    fun encode(byteArray: ByteArray): String = Base64.encodeBytes(byteArray)

    fun encodeUrlSafe(byteArray: ByteArray): String = Base64.encodeBytes(byteArray, Base64.URL_SAFE)
}