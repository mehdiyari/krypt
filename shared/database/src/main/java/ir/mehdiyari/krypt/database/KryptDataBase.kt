package ir.mehdiyari.krypt.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ir.mehdiyari.krypt.accounts.data.dao.AccountsDao
import ir.mehdiyari.krypt.accounts.data.entity.AccountEntity
import ir.mehdiyari.krypt.backup.data.dao.BackupDao
import ir.mehdiyari.krypt.backup.data.entity.BackupEntity
import ir.mehdiyari.krypt.file.data.dao.FilesDao
import ir.mehdiyari.krypt.file.data.entity.FileEntity
import ir.mehdiyari.krypt.file.data.mappers.FileTypeEnumMapper

@Database(
    entities = [
        AccountEntity::class, FileEntity::class, BackupEntity::class
    ], version = 2
)
@TypeConverters(FileTypeEnumMapper::class)
internal abstract class KryptDataBase : RoomDatabase() {
    abstract fun accountsDAO(): AccountsDao
    abstract fun filesDAO(): FilesDao
    abstract fun backupDao(): BackupDao


    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `backups` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `filePath` TEXT NOT NULL, `dateTime` INTEGER NOT NULL, `account` TEXT NOT NULL)")
            }
        }

    }
}