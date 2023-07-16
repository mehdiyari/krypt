package ir.mehdiyari.krypt.data.repositories.account

interface AccountsRepository {

    suspend fun addAccount(
        name: String,
        password: String,
        passwordConfig: String
    ): Pair<Boolean, Throwable?>

    suspend fun getAllAccountsName(): List<String>

    suspend fun isAccountExists(): Boolean

    suspend fun login(
        accountName: String,
        password: String
    ): Boolean

    suspend fun validatePassword(
        password: String
    ): Boolean

    suspend fun deleteCurrentAccount()
}