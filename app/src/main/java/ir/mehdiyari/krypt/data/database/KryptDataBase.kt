package ir.mehdiyari.krypt.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.mehdiyari.krypt.data.account.AccountEntity
import ir.mehdiyari.krypt.data.account.AccountsDao
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnumMapper
import ir.mehdiyari.krypt.data.file.FilesDao

@Database(
    entities = [
        AccountEntity::class, FileEntity::class
    ], version = 1
)
@TypeConverters(FileTypeEnumMapper::class)
abstract class KryptDataBase : RoomDatabase() {
    abstract fun accountsDAO(): AccountsDao
    abstract fun filesDAO(): FilesDao
}