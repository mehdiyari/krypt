package ir.mehdiyari.krypt.setting.data

import ir.mehdiyari.krypt.account.api.CurrentUserManager
import ir.mehdiyari.krypt.account.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.utils.FilesUtilities
import javax.inject.Inject

internal class DeleteAccountHelper @Inject constructor(
    private val accountRepository: AccountsRepository,
    private val currentUserManager: CurrentUserManager,
    private val filesRepository: FilesRepository,
    private val backupCacheDeleteDelegate: BackupCacheDeleteDelegate,
    private val fileUtils: FilesUtilities
) {
    suspend fun clearCurrentAccount() {
        filesRepository.deleteEncryptedFilesFromKryptDBAndFileSystem(
            filesRepository.getAllFiles()
        )
        fileUtils.deleteCacheDir()
        fileUtils.deleteCachedVideoDIR()
        accountRepository.deleteCurrentAccount()
        backupCacheDeleteDelegate.deleteCachedBackupFiles()
        currentUserManager.clearCurrentUser()
    }
}