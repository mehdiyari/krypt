package ir.mehdiyari.krypt.data.account

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface AccountsDao {

    @Insert(entity = AccountEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(accountEntity: AccountEntity)

    @Query("select * from accounts")
    suspend fun getAccounts(): List<AccountEntity>

    @Query("SELECT count(*) from accounts")
    suspend fun getAccountsSize(): Int

    suspend fun isAnyAccountExist(): Boolean = getAccountsSize() > 0
}