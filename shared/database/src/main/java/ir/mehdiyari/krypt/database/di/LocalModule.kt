package ir.mehdiyari.krypt.database.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.accounts.data.dao.AccountsDao
import ir.mehdiyari.krypt.backup.data.dao.BackupDao
import ir.mehdiyari.krypt.database.KryptDataBase
import ir.mehdiyari.krypt.file.data.dao.FilesDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class LocalModule {

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