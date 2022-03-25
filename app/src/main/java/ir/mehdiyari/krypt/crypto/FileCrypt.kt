package ir.mehdiyari.krypt.crypto

import ir.mehdiyari.krypt.crypto.SymmetricHelper.Companion.INITIALIZE_VECTOR_SIZE
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject

class FileCrypt @Inject constructor(
    private val key: dagger.Lazy<SecretKey>,
    private val symmetricHelper: SymmetricHelper
) {

    companion object {
        const val BUFFER_SIZE = 8 * 1024
    }

    fun encryptFileToPath(
        sourcePath: String,
        destinationPath: String
    ): Boolean {
        return try {
            FileInputStream(sourcePath).use { sourceInputStream ->
                val cipher = Cipher.getInstance(SymmetricHelper.AES_CBC_PKS5PADDING)
                val initVector = symmetricHelper.createInitVector()
                cipher.init(Cipher.ENCRYPT_MODE, key.get(), IvParameterSpec(initVector))
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

                true
            }
        } catch (t: Throwable) {
            false
        }
    }

    fun decryptFileToPath(
        sourcePath: String,
        destinationPath: String
    ): Boolean {
        return try {
            FileOutputStream(destinationPath).use { outputFileStream ->
                val cipher = Cipher.getInstance(SymmetricHelper.AES_CBC_PKS5PADDING)
                val realFileInputStream = FileInputStream(sourcePath)
                val initVector = ByteArray(INITIALIZE_VECTOR_SIZE)
                realFileInputStream.read(initVector)

                cipher.init(Cipher.DECRYPT_MODE, key.get(), IvParameterSpec(initVector))
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

            true
        } catch (t: Throwable) {
            false
        }
    }

    fun encryptString(data: ByteArray): ByteArray {
        val initVector = symmetricHelper.createInitVector()
        val encryptedTexts = symmetricHelper.encrypt(
            algorithmName = SymmetricHelper.AES_CBC_PKS5PADDING,
            data = data,
            key = key.get(),
            initVector = initVector
        )

        return initVector.combineWith(encryptedTexts)
    }

    fun decryptString(data: ByteArray): ByteArray? {
        val initVector = data.getBeforeIndex(INITIALIZE_VECTOR_SIZE)
        return symmetricHelper.decrypt(
            algorithmName = SymmetricHelper.AES_CBC_PKS5PADDING,
            encryptedData = data.getAfterIndex(INITIALIZE_VECTOR_SIZE),
            key = key.get(),
            initVector = initVector
        )
    }
}