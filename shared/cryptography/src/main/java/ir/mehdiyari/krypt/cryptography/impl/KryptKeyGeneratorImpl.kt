package ir.mehdiyari.krypt.cryptography.impl

import ir.mehdiyari.krypt.cryptography.api.KryptKeyGenerator
import ir.mehdiyari.krypt.cryptography.utils.HashingUtils
import ir.mehdiyari.krypt.cryptography.utils.toUtf8Bytes
import ir.mehdiyari.krypt.dispatchers.di.DispatchersQualifierType
import ir.mehdiyari.krypt.dispatchers.di.DispatchersType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class KryptKeyGeneratorImpl @Inject constructor(
    private val hashingUtils: HashingUtils,
    @DispatchersType(DispatchersQualifierType.IO) private val ioDispatcher: CoroutineDispatcher,
) : KryptKeyGenerator {

    override suspend fun generateKey(data: String, salt: ByteArray): Result<ByteArray> {
        return withContext(ioDispatcher) {
            try {
                Result.success(hashingUtils.hash(HashingUtils.SHA_256, data.toUtf8Bytes(), salt))
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }

}