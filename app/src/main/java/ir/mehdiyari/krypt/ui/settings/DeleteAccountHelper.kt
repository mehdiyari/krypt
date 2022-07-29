package ir.mehdiyari.krypt.ui.settings

import ir.mehdiyari.krypt.data.repositories.AccountsRepository
import ir.mehdiyari.krypt.data.repositories.CurrentUser
import ir.mehdiyari.krypt.data.repositories.FilesRepository
import ir.mehdiyari.krypt.data.repositories.backup.BackupRepository
import ir.mehdiyari.krypt.utils.FilesUtilities
import javax.inject.Inject

class DeleteAccountHelper @Inject constructor(
    private val accountRepository: AccountsRepository,
    private val currentAccount: CurrentUser,
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
        currentAccount.clear()
    }
}