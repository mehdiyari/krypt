package ir.mehdiyari.krypt.backup.logic.restore

import javax.crypto.SecretKey

interface RestoreRepository {
    suspend fun restoreAll(backupFile: String, key: SecretKey): Result<Unit>
}

