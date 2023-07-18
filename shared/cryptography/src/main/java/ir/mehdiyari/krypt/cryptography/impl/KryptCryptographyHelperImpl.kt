package ir.mehdiyari.krypt.cryptography.impl

import ir.mehdiyari.krypt.cryptography.api.ByteCryptography
import ir.mehdiyari.krypt.cryptography.api.FileCryptography
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import javax.crypto.SecretKey
import javax.inject.Inject

internal class KryptCryptographyHelperImpl @Inject constructor(
    private val userKeyProvider: () -> SecretKey?,
    private val byteCryptography: ByteCryptography,
    private val fileCryptography: FileCryptography,
) : KryptCryptographyHelper {

    override suspend fun encryptFile(sourcePath: String, destinationPath: String): Result<Unit> =
        fileCryptography.encryptFile(
            sourcePath = sourcePath,
            destinationPath = destinationPath,
            key = userKeyProvider()!!,
        )

    override suspend fun decryptFile(sourcePath: String, destinationPath: String): Result<Unit> =
        fileCryptography.decryptFile(
            sourcePath = sourcePath,
            destinationPath = destinationPath,
            key = userKeyProvider()!!,
        )

    override suspend fun encryptBytes(bytes: ByteArray, initVector: ByteArray): Result<ByteArray> =
        byteCryptography.encryptBytes(
            bytes = bytes,
            initVector = initVector,
            key = userKeyProvider()!!,
        )

    override suspend fun decryptBytes(bytes: ByteArray, initVector: ByteArray): Result<ByteArray> =
        byteCryptography.decryptBytes(
            bytes = bytes,
            initVector = initVector,
            key = userKeyProvider()!!
        )
}