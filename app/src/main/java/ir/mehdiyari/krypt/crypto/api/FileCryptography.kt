package ir.mehdiyari.krypt.crypto.api

import javax.crypto.SecretKey

interface FileCryptography {

    suspend fun encryptFile(
        sourcePath: String,
        destinationPath: String,
        key: SecretKey
    ): Result<Unit>

    suspend fun decryptFile(
        sourcePath: String,
        destinationPath: String,
        key: SecretKey
    ): Result<Unit>

}