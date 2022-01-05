package ir.mehdiyari.krypt.data.repositories

import ir.mehdiyari.krypt.crypto.*
import ir.mehdiyari.krypt.data.account.AccountEntity
import ir.mehdiyari.krypt.data.account.AccountsDao
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsRepository @Inject constructor(
    private val accountsDao: AccountsDao,
    private val symmetricHelper: SymmetricHelper,
    private val passwordKeyGenerator: PasswordKeyGenerator,
) {
    suspend fun addAccount(name: String, password: String): Pair<Boolean, Throwable?> {
        if (name.trim().length < 5) return false to BadAccountNameThrowable()
        if (password.trim().length < 12) return false to PasswordLengthThrowable()

        val salt = passwordKeyGenerator.generateSalt()
        val iv = symmetricHelper.createInitVector()
        val key = passwordKeyGenerator.generate32BytesKeyFromPassword(password, salt)

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

    suspend fun getAllAccountsName(): List<String> = accountsDao.getAccounts().map { it.name }

    suspend fun isAccountExists(): Boolean = accountsDao.isAnyAccountExist()
}