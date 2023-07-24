package ir.mehdiyari.krypt.ui.settings

import ir.mehdiyari.krypt.account.api.CurrentUserManager
import ir.mehdiyari.krypt.account.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.data.repositories.backup.BackupRepository
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.repositories.utils.FilesUtilities
import javax.inject.Inject

class DeleteAccountHelper @Inject constructor(
    private val accountRepository: AccountsRepository,
    private val currentUserManager: CurrentUserManager,
    private val filesRepository: FilesRepository,
    private val backupRepository: BackupRepository,
    private val fileUtils: FilesUtilities
) {
    suspend fun clearCurrentAccount() {
        filesRepository.deleteEncryptedFilesFromKryptDBAndFileSystem(
            filesRepository.getAllFiles()
        )
        fileUtils.deleteCacheDir()
        fileUtils.deleteCachedVideoDIR()
        accountRepository.deleteCurrentAccount()
        backupRepository.deleteCachedBackupFiles()
        currentUserManager.clearCurrentUser()
    }
}