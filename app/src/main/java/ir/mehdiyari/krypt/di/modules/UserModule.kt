package ir.mehdiyari.krypt.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.app.user.UserKeyProvider
import ir.mehdiyari.krypt.app.user.UsernameProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {

    @Binds
    abstract fun provideUsernameProvider(
        currentUserManager: ir.mehdiyari.krypt.app.CurrentUserManager
    ): UsernameProvider

    @Binds
    abstract fun provideUserKeyProvider(
        currentUserManager: ir.mehdiyari.krypt.app.CurrentUserManager
    ): UserKeyProvider

    @Binds
    abstract fun provideCurrentUserManager(
        currentUserManager: ir.mehdiyari.krypt.app.CurrentUserManager
    ): ir.mehdiyari.krypt.app.user.CurrentUserManager

}