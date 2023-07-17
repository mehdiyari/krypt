package ir.mehdiyari.krypt.ui.settings

import ir.mehdiyari.krypt.app.user.CurrentUserManager
import ir.mehdiyari.krypt.data.repositories.account.AccountsRepository
import ir.mehdiyari.krypt.data.repositories.backup.BackupRepository
import ir.mehdiyari.krypt.data.repositories.files.FilesRepository
import ir.mehdiyari.krypt.utils.FilesUtilities
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