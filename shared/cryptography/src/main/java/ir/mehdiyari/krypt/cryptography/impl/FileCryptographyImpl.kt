package ir.mehdiyari.krypt.cryptography.impl

import ir.mehdiyari.krypt.cryptography.api.FileCryptography
import ir.mehdiyari.krypt.cryptography.utils.SymmetricHelper
import ir.mehdiyari.krypt.dispatchers.di.DispatchersQualifierType
import ir.mehdiyari.krypt.dispatchers.di.DispatchersType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

const val BUFFER_SIZE = 8 * 1024

internal class FileCryptographyImpl @Inject constructor(
    @DispatchersType(DispatchersQualifierType.IO) private val ioDispatcher: CoroutineDispatcher,
    private val symmetricHelper: SymmetricHelper,
) : FileCryptography {

    override suspend fun encryptFile(
        sourcePath: String,
        destinationPath: String,
        key: SecretKey
    ): Result<Unit> = encryptFile(FileInputStream(File(sourcePath)), destinationPath, key)

    override suspend fun encryptFile(
        stream: FileInputStream,
        destinationPath: String,
        key: SecretKey
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            stream.use { sourceInputStream ->
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
    ): Result<Unit> = decryptFile(FileInputStream(File(sourcePath)), destinationPath, key)

    override suspend fun decryptFile(
        stream: FileInputStream,
        destinationPath: String,
        key: SecretKey
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            FileOutputStream(destinationPath).use { outputFileStream ->
                val cipher = Cipher.getInstance(SymmetricHelper.AES_CBC_PKS5PADDING)
                val initVector = ByteArray(SymmetricHelper.INITIALIZE_VECTOR_SIZE)
                stream.read(initVector)

                cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(initVector))
                CipherInputStream(stream, cipher).use { cipherInputStream ->
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