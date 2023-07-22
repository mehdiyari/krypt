package ir.mehdiyari.krypt.account.data.repositories

import ir.mehdiyari.krypt.account.api.CurrentUserManager
import ir.mehdiyari.krypt.account.api.UserKeyProvider
import ir.mehdiyari.krypt.account.api.UsernameProvider
import ir.mehdiyari.krypt.account.data.local.AccountEntity
import ir.mehdiyari.krypt.account.data.local.AccountsDao
import ir.mehdiyari.krypt.account.exeptions.BadAccountNameThrowable
import ir.mehdiyari.krypt.account.exeptions.PasswordLengthThrowable
import ir.mehdiyari.krypt.account.exeptions.PasswordsNotMatchThrowable
import ir.mehdiyari.krypt.cryptography.api.KryptKeyGenerator
import ir.mehdiyari.krypt.cryptography.utils.Base64
import ir.mehdiyari.krypt.cryptography.utils.HashingUtils
import ir.mehdiyari.krypt.cryptography.utils.SymmetricHelper
import ir.mehdiyari.krypt.cryptography.utils.combineWith
import ir.mehdiyari.krypt.cryptography.utils.getAfterIndex
import ir.mehdiyari.krypt.cryptography.utils.getBeforeIndex
import ir.mehdiyari.krypt.cryptography.utils.getBytesBetweenIndexes
import ir.mehdiyari.krypt.cryptography.utils.toUtf8Bytes
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AccountsRepositoryImpl @Inject constructor(
    private val accountsDao: AccountsDao,
    private val symmetricHelper: SymmetricHelper,
    private val kryptKeyGenerator: KryptKeyGenerator,
    private val currentUserManager: CurrentUserManager,
    private val usernameProvider: UsernameProvider,
    private val userKeyProvider: UserKeyProvider,
    private val hashingUtils: HashingUtils,
) : AccountsRepository {

    override suspend fun addAccount(
        name: String,
        password: String,
        passwordConfig: String
    ): Pair<Boolean, Throwable?> {
        if (name.trim().length < 5) return false to BadAccountNameThrowable()
        if (password.trim().length < 12) return false to PasswordLengthThrowable()
        if (password != passwordConfig) return false to PasswordsNotMatchThrowable()

        val salt = hashingUtils.generateRandomSalt()
        val iv = symmetricHelper.createInitVector()
        val result = kryptKeyGenerator.generateKey(password, salt)
        if (result.isFailure) return false to result.exceptionOrNull()

        val key = result.getOrThrow()

        val encryptedNameBytes = symmetricHelper.encrypt(
            data = name.toUtf8Bytes(),
            key = SecretKeySpec(key, "AES"),
            initVector = iv
        )

        val finalBytes = encryptedNameBytes.combineWith(salt, iv)

        accountsDao.insert(
            AccountEntity(
                name,
                Base64.encodeBytes(finalBytes)
            )
        )


        return true to null
    }

    override suspend fun getAllAccountsName(): List<String> =
        accountsDao.getAccounts().map { it.name }

    override suspend fun isAccountExists(): Boolean = accountsDao.isAnyAccountExist()

    override suspend fun login(
        accountName: String,
        password: String
    ): Boolean {
        currentUserManager.clearCurrentUser()
        val account = accountsDao.getAccountWithName(accountName) ?: return false
        val nameData = Base64.decode(account.encryptedName)
        val iv = nameData.getAfterIndex(nameData.size - 16)
        val salt = nameData.getBytesBetweenIndexes(
            nameData.size - (SymmetricHelper.INITIALIZE_VECTOR_SIZE + HashingUtils.SALT_SIZE),
            nameData.size - SymmetricHelper.INITIALIZE_VECTOR_SIZE
        )

        val encryptedName = nameData.getBeforeIndex(nameData.size - 32)

        val keyAsBytes = kryptKeyGenerator.generateKey(password, salt).getOrThrow()
        val secretKey = SecretKeySpec(
            keyAsBytes,
            "AES"
        )

        val decryptedName = try {
            symmetricHelper.decrypt(
                encryptedData = encryptedName,
                key = secretKey,
                initVector = iv
            ) ?: return false
        } catch (t: Throwable) {
            return false
        }

        val decryptedStrName = String(decryptedName)

        return if (decryptedStrName.trim() == accountName.trim()) {
            currentUserManager.setCurrentUser(accountName.trim(), keyAsBytes)
            true
        } else {
            false
        }
    }

    override suspend fun validatePassword(
        password: String
    ): Boolean {
        val account =
            accountsDao.getAccountWithName(usernameProvider.getUsername()!!) ?: return false
        val nameData = Base64.decode(account.encryptedName)
        val salt = nameData.getBytesBetweenIndexes(
            nameData.size - 32, nameData.size - 16
        )
        val keyBytes = kryptKeyGenerator.generateKey(
            password, salt
        ).getOrThrow()

        return SecretKeySpec(keyBytes, "AES") == userKeyProvider.getKey()
    }

    override suspend fun deleteCurrentAccount() {
        accountsDao.deleteCurrentAccount(usernameProvider.getUsername()!!)
    }
}