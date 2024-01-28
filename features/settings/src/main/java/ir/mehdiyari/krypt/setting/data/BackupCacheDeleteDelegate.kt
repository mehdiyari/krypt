package ir.mehdiyari.krypt.setting.data

interface BackupCacheDeleteDelegate {

    suspend fun deleteCachedBackupFiles()

}