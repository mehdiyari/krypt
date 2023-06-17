package ir.mehdiyari.krypt.crypto

import ir.mehdiyari.krypt.crypto.utils.HashingUtils
import ir.mehdiyari.krypt.crypto.utils.toUtf8Bytes
import javax.inject.Inject

class PasswordKeyGenerator @Inject constructor(
    private val hashingUtils: HashingUtils
) {
    fun generate32BytesKeyFromPassword(
        password: String, salt: ByteArray
    ): ByteArray = hashingUtils.hash(
        data = password.trim().toUtf8Bytes(),
        salt = salt
    )

    fun generateSalt(): ByteArray = hashingUtils.generateRandomSalt()
}