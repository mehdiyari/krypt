package ir.mehdiyari.krypt.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.data.repositories.account.AccountsRepository
import ir.mehdiyari.krypt.data.repositories.account.DefaultAccountsRepository
import ir.mehdiyari.krypt.data.repositories.files.DefaultFilesRepository
import ir.mehdiyari.krypt.data.repositories.files.FilesRepository
import ir.mehdiyari.krypt.data.repositories.settings.DefaultSettingsRepository
import ir.mehdiyari.krypt.data.repositories.settings.SettingsRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindSettingsRepository(impl: DefaultSettingsRepository): SettingsRepository

    @Binds
    abstract fun bindAccountRepository(impl: DefaultAccountsRepository): AccountsRepository

    @Binds
    abstract fun bindFilesRepository(impl: DefaultFilesRepository): FilesRepository
}