package ir.mehdiyari.krypt.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class SplashModule {

    @Provides
    @SplashDelay
    fun provideSplashDelay(): Long = 1000L

}