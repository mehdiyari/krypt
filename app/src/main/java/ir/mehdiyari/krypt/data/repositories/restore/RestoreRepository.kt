package ir.mehdiyari.krypt.data.repositories.restore

import javax.crypto.SecretKey

interface RestoreRepository {
    suspend fun restoreAll(backupFile: String, key: SecretKey): Result<Boolean>
}

