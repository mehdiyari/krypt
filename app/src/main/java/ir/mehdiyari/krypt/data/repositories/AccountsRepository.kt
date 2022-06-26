package ir.mehdiyari.krypt.data.repositories

import ir.mehdiyari.krypt.crypto.*
import ir.mehdiyari.krypt.data.account.AccountEntity
import ir.mehdiyari.krypt.data.account.AccountsDao
import ir.mehdiyari.krypt.ui.logout.throwables.BadAccountNameThrowable
import ir.mehdiyari.krypt.ui.logout.throwables.PasswordLengthThrowable
import ir.mehdiyari.krypt.ui.logout.throwables.PasswordsNotMatchThrowable
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsRepository @Inject constructor(
    private val accountsDao: AccountsDao,
    private val symmetricHelper: SymmetricHelper,
    private val passwordKeyGenerator: PasswordKeyGenerator,
    private val currentUser: CurrentUser
) {
    suspend fun addAccount(
        name: String,
        password: String,
        passwordConfig: String
    ): Pair<Boolean, Throwable?> {
        if (name.trim().length < 5) return false to BadAccountNameThrowable()
        if (password.trim().length < 12) return false to PasswordLengthThrowable()
        if (password != passwordConfig) return false to PasswordsNotMatchThrowable()

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

    suspend fun login(
        accountName: String,
        password: String
    ): Boolean {
        currentUser.clear()
        val account = accountsDao.getAccountWithName(accountName) ?: return false
        val nameData = Base64.decode(account.encryptedName)
        val iv = nameData.getAfterIndex(nameData.size - 16)
        val salt = nameData.getBytesBetweenIndexes(
            nameData.size - 32, nameData.size - 16
        )

        val encryptedName = nameData.getBeforeIndex(nameData.size - 32)

        val keyAsBytes = passwordKeyGenerator.generate32BytesKeyFromPassword(password, salt)
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
            currentUser.accountName = accountName.trim()
            currentUser.key = keyAsBytes
            true
        } else {
            false
        }
    }

    suspend fun validatePassword(
        password: String
    ): Boolean {
        val account = accountsDao.getAccountWithName(currentUser.accountName!!) ?: return false
        val nameData = Base64.decode(account.encryptedName)
        val salt = nameData.getBytesBetweenIndexes(
            nameData.size - 32, nameData.size - 16
        )
        val keyBytes = passwordKeyGenerator.generate32BytesKeyFromPassword(
            password, salt
        )

        return keyBytes.contentEquals(currentUser.key)
    }

    suspend fun deleteCurrentAccount() {
        accountsDao.deleteCurrentAccount(currentUser.accountName!!)
    }
}