package ir.mehdiyari.krypt.data.repositories.backup

import ir.mehdiyari.krypt.data.backup.BackupEntity

interface BackupRepository {
    suspend fun backupAll(): Boolean
    suspend fun getBackupRecord(): List<BackupEntity>
    suspend fun getLastBackUpDateTime(): String
    fun convertToBackUpDateTimeFormat(dateTime: Long): String
    suspend fun deleteBackupWithId(backupFileId: Int)
    suspend fun getBackupFilePathWithId(backupFileId: Int): String
    suspend fun deleteCachedBackupFiles()
}