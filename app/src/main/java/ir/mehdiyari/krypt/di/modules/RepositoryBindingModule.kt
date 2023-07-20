package ir.mehdiyari.krypt.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.data.repositories.account.AccountsRepository
import ir.mehdiyari.krypt.data.repositories.account.AccountsRepositoryImpl
import ir.mehdiyari.krypt.data.repositories.backup.BackupRepository
import ir.mehdiyari.krypt.data.repositories.backup.BackupRepositoryImpl
import ir.mehdiyari.krypt.data.repositories.files.FilesRepository
import ir.mehdiyari.krypt.data.repositories.files.FilesRepositoryImpl
import ir.mehdiyari.krypt.data.repositories.settings.SettingsRepository
import ir.mehdiyari.krypt.data.repositories.settings.SettingsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {
    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindAccountRepository(impl: AccountsRepositoryImpl): AccountsRepository

    @Binds
    abstract fun bindFilesRepository(impl: FilesRepositoryImpl): FilesRepository

    @Binds
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository
}