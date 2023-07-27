package ir.mehdiyari.krypt.backup.logic.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.backup.logic.backup.BackupRepository
import ir.mehdiyari.krypt.backup.logic.backup.BackupRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BackupModule {

    @Binds
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository

}