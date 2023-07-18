package ir.mehdiyari.krypt.crypto.impl

import ir.mehdiyari.krypt.app.user.UserKeyProvider
import ir.mehdiyari.krypt.crypto.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.cryptography.api.ByteCryptography
import ir.mehdiyari.krypt.cryptography.api.FileCryptography
import javax.inject.Inject

class KryptCryptographyHelperImpl @Inject constructor(
    private val userKeyProvider: UserKeyProvider,
    private val byteCryptography: ByteCryptography,
    private val fileCryptography: FileCryptography,
) : KryptCryptographyHelper {

    override suspend fun encryptFile(sourcePath: String, destinationPath: String): Result<Unit> =
        fileCryptography.encryptFile(
            sourcePath = sourcePath,
            destinationPath = destinationPath,
            key = userKeyProvider.getKey()!!,
        )

    override suspend fun decryptFile(sourcePath: String, destinationPath: String): Result<Unit> =
        fileCryptography.decryptFile(
            sourcePath = sourcePath,
            destinationPath = destinationPath,
            key = userKeyProvider.getKey()!!,
        )

    override suspend fun encryptBytes(bytes: ByteArray, initVector: ByteArray): Result<ByteArray> =
        byteCryptography.encryptBytes(
            bytes = bytes,
            initVector = initVector,
            key = userKeyProvider.getKey()!!,
        )

    override suspend fun decryptBytes(bytes: ByteArray, initVector: ByteArray): Result<ByteArray> =
        byteCryptography.decryptBytes(
            bytes = bytes,
            initVector = initVector,
            key = userKeyProvider.getKey()!!
        )
}