package ir.mehdiyari.krypt.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.app.KryptApplication
import ir.mehdiyari.krypt.app.user.CurrentUserManager
import ir.mehdiyari.krypt.app.user.UserKeyProvider
import ir.mehdiyari.krypt.app.user.UsernameProvider

@Module
@InstallIn(SingletonComponent::class)
class UserModule {

    @Provides
    fun provideUsernameProvider(
        application: Application
    ): UsernameProvider = (application as KryptApplication)

    @Provides
    fun provideUserKeyProvider(
        application: Application
    ): UserKeyProvider = (application as KryptApplication)

    @Provides
    fun provideCurrentUserManager(
        application: Application
    ): CurrentUserManager = (application as KryptApplication)

}