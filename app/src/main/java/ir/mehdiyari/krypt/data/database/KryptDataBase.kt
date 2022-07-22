package ir.mehdiyari.krypt.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ir.mehdiyari.krypt.data.account.AccountEntity
import ir.mehdiyari.krypt.data.account.AccountsDao
import ir.mehdiyari.krypt.data.backup.BackupDao
import ir.mehdiyari.krypt.data.backup.BackupEntity
import ir.mehdiyari.krypt.data.file.FileEntity
import ir.mehdiyari.krypt.data.file.FileTypeEnumMapper
import ir.mehdiyari.krypt.data.file.FilesDao

@Database(
    entities = [
        AccountEntity::class, FileEntity::class, BackupEntity::class
    ], version = 2
)
@TypeConverters(FileTypeEnumMapper::class)
abstract class KryptDataBase : RoomDatabase() {
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