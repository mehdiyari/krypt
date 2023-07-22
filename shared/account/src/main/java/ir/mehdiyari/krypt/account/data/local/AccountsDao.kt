package ir.mehdiyari.krypt.account.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AccountsDao {

    @Insert(entity = AccountEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(accountEntity: AccountEntity)

    @Query("select * from accounts")
    suspend fun getAccounts(): List<AccountEntity>

    @Query("SELECT count(*) from accounts")
    suspend fun getAccountsSize(): Int

    suspend fun isAnyAccountExist(): Boolean = getAccountsSize() > 0

    @Query("SELECT * from accounts where name = :accountName")
    suspend fun getAccountWithName(accountName: String): AccountEntity?

    @Query("DELETE FROM accounts where name = :accountName")
    suspend fun deleteCurrentAccount(accountName: String)

}