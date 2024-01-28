package ir.mehdiyari.krypt.backup.logic.backup

import ir.mehdiyari.krypt.accounts.data.entity.AccountEntity
import ir.mehdiyari.krypt.file.data.entity.FileEntity

internal data class DBBackupModel(
    val account: AccountEntity,
    val files: List<FileEntity>
)