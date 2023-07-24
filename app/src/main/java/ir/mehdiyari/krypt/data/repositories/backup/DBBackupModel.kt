package ir.mehdiyari.krypt.data.repositories.backup

import ir.mehdiyari.krypt.accounts.data.entity.AccountEntity
import ir.mehdiyari.krypt.file.data.entity.FileEntity

data class DBBackupModel(
    val account: AccountEntity,
    val files: List<FileEntity>
)