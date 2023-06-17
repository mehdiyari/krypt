package ir.mehdiyari.krypt.crypto.api

import java.io.FileInputStream
import javax.crypto.SecretKey

interface FileCryptography {

    suspend fun encryptFile(
        stream: FileInputStream,
        destinationPath: String,
        key: SecretKey
    ): Result<Unit>

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

    suspend fun decryptFile(
        stream: FileInputStream,
        destinationPath: String,
        key: SecretKey
    ): Result<Unit>

}