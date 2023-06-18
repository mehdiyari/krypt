package ir.mehdiyari.krypt.crypto.impl

import ir.mehdiyari.krypt.crypto.api.KryptKeyGenerator
import ir.mehdiyari.krypt.crypto.utils.HashingUtils
import ir.mehdiyari.krypt.crypto.utils.toUtf8Bytes
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KryptKeyGeneratorImpl @Inject constructor(
    private val hashingUtils: HashingUtils,
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
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