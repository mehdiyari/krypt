package ir.mehdiyari.krypt.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.mehdiyari.krypt.data.account.AccountEntity
import ir.mehdiyari.krypt.data.account.AccountsDao

@Database(entities = [AccountEntity::class], version = 1)
abstract class KryptDataBase: RoomDatabase() {
    abstract fun accountsDAO(): AccountsDao
}