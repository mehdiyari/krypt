package ir.mehdiyari.krypt.data.repositories

import ir.mehdiyari.krypt.crypto.utils.Base64
import ir.mehdiyari.krypt.crypto.utils.HashingUtils
import ir.mehdiyari.krypt.crypto.utils.SymmetricHelper
import ir.mehdiyari.krypt.crypto.utils.getBytesBetweenIndexes
import javax.inject.Inject

class Base64Wrapper @Inject constructor() {
    fun decode(encryptedName: String): ByteArray =
        Base64.decode(encryptedName)
            .let { name ->
                name.getBytesBetweenIndexes(
                    start = name.size - (SymmetricHelper.INITIALIZE_VECTOR_SIZE + HashingUtils.SALT_SIZE),
                    end = name.size - SymmetricHelper.INITIALIZE_VECTOR_SIZE
                )
            }
}