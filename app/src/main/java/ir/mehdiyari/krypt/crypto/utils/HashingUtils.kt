package ir.mehdiyari.krypt.crypto.utils

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HashingUtils @Inject constructor() {

    companion object {
        const val MD5: String = "MD5"
        const val SHA_256 = "SHA-256"

        /**
         * Default Size of Salt [https://en.wikipedia.org/wiki/Salt_(cryptography)]
         */
        const val SALT_SIZE = 16
    }

    fun generateRandomSalt(size: Int = SALT_SIZE): ByteArray = SecureRandom().let {
        val generatedSalt = ByteArray(size)
        it.nextBytes(generatedSalt)
        generatedSalt
    }

    fun hash(
        algorithmName: String = SHA_256,
        data: ByteArray,
        salt: ByteArray? = generateRandomSalt()
    ): ByteArray = MessageDigest.getInstance(algorithmName).let {
        it.digest(ByteArrayOutputStream().apply {
            write(salt?.combineWith(data) ?: data)
        }.toByteArray())
    }

    fun getSha256FileHash(filePath: String): String {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val digest = MessageDigest.getInstance("SHA-256")
        val bis = BufferedInputStream(FileInputStream(filePath))

        while (true) {
            val count = bis.read(buffer)
            if (count == -1)
                break
            else
                digest.update(buffer, 0, count)
        }

        bis.close()

        return Base64.encodeBytes(digest.digest())
    }
}