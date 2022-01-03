package ir.mehdiyari.krypt.data.repositories

import ir.mehdiyari.krypt.data.account.AccountEntity
import ir.mehdiyari.krypt.data.account.AccountsDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountsRepository @Inject constructor(
    private val accountsDao: AccountsDao
) {
    suspend fun addAccount(name: String) {
        accountsDao.insert(AccountEntity(name, ""))
    }

    suspend fun getAllAccountsName(): List<String> = accountsDao.getAccounts().map { it.name }

    suspend fun isAccountExists(): Boolean = accountsDao.isAnyAccountExist()
}