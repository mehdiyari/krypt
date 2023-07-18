package ir.mehdiyari.krypt.ui.restore

import ir.mehdiyari.krypt.cryptography.api.KryptKeyGenerator
import ir.mehdiyari.krypt.cryptography.utils.HashingUtils
import java.io.File
import java.io.FileInputStream
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class RestoreKeyGenerator @Inject constructor(
    private val kryptKeyGenerator: KryptKeyGenerator,
) {

    suspend fun generateKey(data: String, filePath: String): SecretKey {
        val salt = ByteArray(HashingUtils.SALT_SIZE)
        FileInputStream(File(filePath)).use {
            it.read(salt, 0, HashingUtils.SALT_SIZE)
            it.close()
        }

        return SecretKeySpec(kryptKeyGenerator.generateKey(data, salt).getOrThrow(), "AES")
    }

}