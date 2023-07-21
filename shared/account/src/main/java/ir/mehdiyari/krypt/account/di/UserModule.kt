package ir.mehdiyari.krypt.account.di

import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.account.api.CurrentUserManager
import ir.mehdiyari.krypt.account.api.UserKeyProvider
import ir.mehdiyari.krypt.account.api.UsernameProvider
import ir.mehdiyari.krypt.account.impl.CurrentUserManagerImpl
import javax.crypto.SecretKey

@Module
@InstallIn(SingletonComponent::class)
abstract class UserBindingModule {

    @Binds
    abstract fun provideUsernameProvider(
        currentUserManagerImpl: CurrentUserManagerImpl
    ): UsernameProvider

    @Binds
    abstract fun provideUserKeyProvider(
        currentUserManagerImpl: CurrentUserManagerImpl
    ): UserKeyProvider

    @Binds
    abstract fun provideCurrentUserManager(
        currentUserManagerImpl: CurrentUserManagerImpl
    ): CurrentUserManager

}

@Module
@InstallIn(SingletonComponent::class)
class UserModule {

    @Provides
    fun provideKeyProviderFunction(
        currentUserManager: Lazy<UserKeyProvider>
    ): Function0<@JvmWildcard SecretKey?> = currentUserManager.get()::getKey

}