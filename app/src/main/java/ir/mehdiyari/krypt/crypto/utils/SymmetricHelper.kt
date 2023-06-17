package ir.mehdiyari.krypt.crypto.utils

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymmetricHelper @Inject constructor() {

    companion object {
        const val AES_CBC_PKS7PADDING = "AES/CBC/PKCS7Padding"
        const val AES_CBC_PKS5PADDING = "AES/CBC/PKCS5Padding"

        /**
         * AES(Advanced-Encryption-Standard)
         *
         * @see <a href="https://en.wikipedia.org/wiki/Advanced_Encryption_Standard">Advanced Encryption Standard</a>
         */
        private const val AES = "AES"

        /**
         * Init Vector Size
         * @see <a href="https://en.wikipedia.org/wiki/Initialization_vector">Initialization Vector</a>
         */
        const val INITIALIZE_VECTOR_SIZE = 16
        private const val DEFAULT_AES_KEY_SIZE = 256
    }


    /**
     * Generate Symmetric key with @param keySize
     * @param keySize size of symmetric key
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class)
    fun generateAESKey(keySize: Int = DEFAULT_AES_KEY_SIZE): SecretKey = KeyGenerator.getInstance(
        AES
    ).apply {
        this.init(keySize, SecureRandom())
    }.generateKey()


    /**
     * Create random bytes for using in encrypt decrypt with AES with Mode CBC
     */
    fun createInitVector(size: Int = INITIALIZE_VECTOR_SIZE): ByteArray = ByteArray(size).apply {
        SecureRandom().nextBytes(this)
    }

    /**
     * Encrypt given data with given key and algorithm
     * @param algorithmName name of algorithm
     * @param data data as byteArray
     * @param key key
     * @param initVector IV
     */
    fun encrypt(
        algorithmName: String = AES_CBC_PKS7PADDING,
        data: ByteArray,
        key: SecretKey = generateAESKey(DEFAULT_AES_KEY_SIZE),
        initVector: ByteArray = createInitVector()
    ): ByteArray = Cipher.getInstance(algorithmName).apply {
        this.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(initVector))
    }.doFinal(data)


    /**
     * Decrypt given encryptedData with given key and algorithm
     * @param algorithmName name of algorithm
     * @param encryptedData encrypted data as byteArray
     * @param key key for decrypting data
     * @param initVector IV
     */
    fun decrypt(
        algorithmName: String = AES_CBC_PKS7PADDING,
        encryptedData: ByteArray,
        key: SecretKey,
        initVector: ByteArray
    ): ByteArray? = Cipher.getInstance(algorithmName).let {
        it.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(initVector))
        it.doFinal(encryptedData)
    }

    fun getAESCipher(): Cipher = Cipher.getInstance(AES_CBC_PKS5PADDING)
}