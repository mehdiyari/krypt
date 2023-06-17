package ir.mehdiyari.krypt.crypto.api

interface KryptKeyGenerator {

    suspend fun generateKey(
        data: String,
        salt: ByteArray,
    ): Result<ByteArray>

}