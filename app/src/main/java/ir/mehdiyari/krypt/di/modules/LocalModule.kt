package ir.mehdiyari.krypt.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.account.data.local.AccountsDao
import ir.mehdiyari.krypt.data.backup.BackupDao
import ir.mehdiyari.krypt.data.database.KryptDataBase
import ir.mehdiyari.krypt.data.file.FilesDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Provides
    @Singleton
    fun provideKryptDataBase(@ApplicationContext context: Context): KryptDataBase =
        Room.databaseBuilder(context, KryptDataBase::class.java, "krypt_db")
            .addMigrations(KryptDataBase.MIGRATION_1_2).build()

    @Provides
    @Singleton
    fun provideFilesDao(
        kryptDataBase: KryptDataBase
    ): FilesDao = kryptDataBase.filesDAO()


    @Provides
    @Singleton
    fun provideAccountsDao(
        kryptDataBase: KryptDataBase
    ): AccountsDao = kryptDataBase.accountsDAO()

    @Provides
    @Singleton
    fun provideBackupDao(
        kryptDataBase: KryptDataBase
    ): BackupDao = kryptDataBase.backupDao()

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Application): SharedPreferences =
        context.getSharedPreferences("krypt", Context.MODE_PRIVATE)
}