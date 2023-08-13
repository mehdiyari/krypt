package ir.mehdiyari.krypt.backup.logic.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.backup.logic.backup.BackupRepository
import ir.mehdiyari.krypt.backup.logic.backup.BackupRepositoryImpl
import ir.mehdiyari.krypt.backup.logic.restore.RestoreRepository
import ir.mehdiyari.krypt.backup.logic.restore.RestoreRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BackupModule {

    @Binds
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository

    @Binds
    abstract fun bindRestoreRepository(impl: RestoreRepositoryImpl): RestoreRepository

}