package ir.mehdiyari.krypt.crypto.impl

import ir.mehdiyari.krypt.crypto.SymmetricHelper
import ir.mehdiyari.krypt.crypto.api.FileCryptography
import ir.mehdiyari.krypt.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class FileCryptographyImpl @Inject constructor(
    @DispatcherIO private val ioDispatcher: CoroutineDispatcher,
    private val symmetricHelper: SymmetricHelper,
) : FileCryptography {

    override suspend fun encryptFile(
        sourcePath: String,
        destinationPath: String,
        key: SecretKey
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            FileInputStream(sourcePath).use { sourceInputStream ->
                val cipher = Cipher.getInstance(SymmetricHelper.AES_CBC_PKS5PADDING)
                val initVector = symmetricHelper.createInitVector()
                cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(initVector))
                val realOutputStream = FileOutputStream(destinationPath)
                realOutputStream.write(initVector)
                CipherOutputStream(realOutputStream, cipher).use { outputStream ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    while (true) {
                        val count = sourceInputStream.read(buffer)
                        if (count > 0) {
                            outputStream.write(buffer, 0, count)
                        } else {
                            break
                        }
                    }
                }

                Result.success(Unit)
            }
        } catch (t: Throwable) {
            ensureActive()
            Result.failure(t)
        }
    }

    override suspend fun decryptFile(
        sourcePath: String,
        destinationPath: String,
        key: SecretKey
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            FileOutputStream(destinationPath).use { outputFileStream ->
                val cipher = Cipher.getInstance(SymmetricHelper.AES_CBC_PKS5PADDING)
                val realFileInputStream = FileInputStream(sourcePath)
                val initVector = ByteArray(SymmetricHelper.INITIALIZE_VECTOR_SIZE)
                realFileInputStream.read(initVector)

                cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(initVector))
                CipherInputStream(realFileInputStream, cipher).use { cipherInputStream ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    while (true) {
                        val count = cipherInputStream.read(buffer)
                        if (count > 0) {
                            outputFileStream.write(buffer, 0, count)
                        } else {
                            break
                        }
                    }
                }
            }

            Result.success(Unit)
        } catch (t: Throwable) {
            ensureActive()
            Result.failure(t)
        }
    }

}