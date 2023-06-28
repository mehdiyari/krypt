package ir.mehdiyari.krypt.crypto.impl

import ir.mehdiyari.krypt.crypto.api.ByteCryptography
import ir.mehdiyari.krypt.crypto.api.FileCryptography
import ir.mehdiyari.krypt.crypto.api.KryptCryptographyHelper
import javax.crypto.SecretKey
import javax.inject.Inject

const val BUFFER_SIZE = 8 * 1024

class KryptCryptographyHelperImpl @Inject constructor(
    private val key: dagger.Lazy<SecretKey>,
    private val byteCryptography: ByteCryptography,
    private val fileCryptography: FileCryptography,
) : KryptCryptographyHelper {

    override suspend fun encryptFile(sourcePath: String, destinationPath: String): Result<Unit> =
        fileCryptography.encryptFile(
            sourcePath = sourcePath,
            destinationPath = destinationPath,
            key = key.get(),
        )

    override suspend fun decryptFile(sourcePath: String, destinationPath: String): Result<Unit> =
        fileCryptography.decryptFile(
            sourcePath = sourcePath,
            destinationPath = destinationPath,
            key = key.get(),
        )

    override suspend fun encryptBytes(bytes: ByteArray, initVector: ByteArray): Result<ByteArray> =
        byteCryptography.encryptBytes(
            bytes = bytes,
            initVector = initVector,
            key = key.get(),
        )

    override suspend fun decryptBytes(bytes: ByteArray, initVector: ByteArray): Result<ByteArray> =
        byteCryptography.decryptBytes(
            bytes = bytes,
            initVector = initVector,
            key = key.get()
        )
}