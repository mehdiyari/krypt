package ir.mehdiyari.krypt.data.backup

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BackupDao {

    @Insert(entity = BackupEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(backupEntity: BackupEntity)

    @Query("select * from backups where account = :account order by dateTime DESC")
    suspend fun getAllBackups(account: String): List<BackupEntity>

    @Query("select dateTime from backups where account = :account order by dateTime DESC")
    suspend fun getLastBackupRecord(account: String): Long?

    @Query("select filePath from backups where account = :account")
    suspend fun getAllBackupFiles(account: String): List<String>?

    @Query("select * from backups where id = :backupFileId and account = :accountName")
    suspend fun getEntityWithId(backupFileId: Int, accountName: String): BackupEntity?

    @Query("delete from backups where id = :backupFileId and account = :accountName")
    suspend fun deleteBackupWithId(backupFileId: Int, accountName: String)

}