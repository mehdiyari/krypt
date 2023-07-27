package ir.mehdiyari.krypt.cryptography.utils

import javax.inject.Inject

class Base64Wrapper @Inject constructor() {

    fun decode(base64String: String): ByteArray =
        Base64.decode(base64String)
            .let { name ->
                name.getBytesBetweenIndexes(
                    start = name.size - (SymmetricHelper.INITIALIZE_VECTOR_SIZE + HashingUtils.SALT_SIZE),
                    end = name.size - SymmetricHelper.INITIALIZE_VECTOR_SIZE
                )
            }

    fun encode(byteArray: ByteArray): String = Base64.encodeBytes(byteArray)

    fun encodeUrlSafe(byteArray: ByteArray): String = Base64.encodeBytes(byteArray, Base64.URL_SAFE)
}