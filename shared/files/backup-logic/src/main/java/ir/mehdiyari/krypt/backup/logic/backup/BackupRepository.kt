package ir.mehdiyari.krypt.backup.logic.backup

import android.net.Uri
import ir.mehdiyari.krypt.backup.data.entity.BackupEntity

interface BackupRepository {
    suspend fun backupAll(path: String): Boolean
    suspend fun getBackupRecord(): List<BackupEntity>
    suspend fun getLastBackUpDateTime(): String
    fun convertToBackUpDateTimeFormat(dateTime: Long): String
    suspend fun deleteBackupWithId(backupFileId: Int)
    suspend fun getBackupFilePathWithId(backupFileId: Int): String
    suspend fun deleteCachedBackupFiles()
}