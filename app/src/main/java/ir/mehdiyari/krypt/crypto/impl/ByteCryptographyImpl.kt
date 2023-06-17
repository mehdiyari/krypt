package ir.mehdiyari.krypt.crypto.impl

import ir.mehdiyari.krypt.crypto.SymmetricHelper
import ir.mehdiyari.krypt.crypto.api.ByteCryptography
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.crypto.SecretKey
import javax.inject.Inject

class ByteCryptographyImpl @Inject constructor(
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val symmetricHelper: SymmetricHelper,
) : ByteCryptography {

    override suspend fun encryptBytes(
        bytes: ByteArray,
        initVector: ByteArray,
        key: SecretKey,
    ): Result<ByteArray> = withContext(ioDispatcher) {
        try {
            val encryptedBytes = symmetricHelper.encrypt(
                algorithmName = SymmetricHelper.AES_CBC_PKS5PADDING,
                data = bytes,
                key = key,
                initVector = initVector
            )

            Result.success(encryptedBytes)
        } catch (t: Throwable) {
            ensureActive()
            Result.failure(t)
        }
    }

    override suspend fun decryptBytes(
        bytes: ByteArray,
        initVector: ByteArray,
        key: SecretKey,
    ): Result<ByteArray> = withContext(ioDispatcher) {
        try {
            val decryptedBytes = symmetricHelper.decrypt(
                algorithmName = SymmetricHelper.AES_CBC_PKS5PADDING,
                encryptedData = bytes,
                key = key,
                initVector = initVector
            )!!

            Result.success(decryptedBytes)
        } catch (t: Throwable) {
            ensureActive()
            Result.failure(t)
        }
    }

}