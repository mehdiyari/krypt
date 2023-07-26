package ir.mehdiyari.krypt.di.modules

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.data.repositories.backup.BackupRepository
import ir.mehdiyari.krypt.data.repositories.backup.BackupRepositoryImpl
import ir.mehdiyari.krypt.setting.data.BackupCacheDeleteDelegate

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {

    @Binds
    abstract fun bindBackupRepository(impl: BackupRepositoryImpl): BackupRepository
}

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideBackupCacheDeleteDelegate(
        backupRepository: BackupRepository
    ): BackupCacheDeleteDelegate = object : BackupCacheDeleteDelegate {
        override suspend fun deleteCachedBackupFiles() {
            backupRepository.deleteCachedBackupFiles()
        }
    }

}