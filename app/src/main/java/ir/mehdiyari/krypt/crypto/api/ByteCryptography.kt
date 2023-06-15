package ir.mehdiyari.krypt.crypto.api

import javax.crypto.SecretKey

interface ByteCryptography {

    suspend fun encryptBytes(
        bytes: ByteArray,
        initVector: ByteArray,
        key: SecretKey,
    ): Result<ByteArray>

    suspend fun decryptBytes(
        bytes: ByteArray,
        initVector: ByteArray,
        key: SecretKey,
    ): Result<ByteArray>

}