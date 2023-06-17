package ir.mehdiyari.krypt.crypto

interface KryptCryptographyHelper {

    suspend fun encryptFile(
        sourcePath: String,
        destinationPath: String,
    ): Result<Unit>

    suspend fun decryptFile(
        sourcePath: String,
        destinationPath: String,
    ): Result<Unit>

    suspend fun encryptBytes(
        bytes: ByteArray,
        initVector: ByteArray,
    ): Result<ByteArray>

    suspend fun decryptBytes(
        bytes: ByteArray,
        initVector: ByteArray,
    ): Result<ByteArray>

}