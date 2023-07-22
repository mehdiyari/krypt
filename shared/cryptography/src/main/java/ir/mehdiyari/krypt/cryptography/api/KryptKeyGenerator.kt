package ir.mehdiyari.krypt.cryptography.api

interface KryptKeyGenerator {

    suspend fun generateKey(
        data: String,
        salt: ByteArray,
    ): Result<ByteArray>

}