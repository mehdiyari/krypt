package ir.mehdiyari.krypt.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.data.account.AccountsDao
import ir.mehdiyari.krypt.data.database.KryptDataBase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Provides
    @Singleton
    fun provideKryptDataBase(@ApplicationContext context: Context): KryptDataBase =
        Room.databaseBuilder(context, KryptDataBase::class.java, "krypt_db").build()

    @Provides
    @Singleton
    fun provideAccountsDao(
        kryptDataBase: KryptDataBase
    ): AccountsDao = kryptDataBase.accountsDAO()

}