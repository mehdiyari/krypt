package ir.mehdiyari.krypt.ui.voice.di

import android.media.MediaPlayer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import ir.mehdiyari.krypt.ui.voice.player.KryptMediaPlayer
import ir.mehdiyari.krypt.ui.voice.player.KryptMediaPlayerImpl

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class MediaPlayerBindingModule {

    @Binds
    abstract fun bindKryptMusicPlayer(
        kryptMusicPlayerImpl: KryptMediaPlayerImpl
    ): KryptMediaPlayer

}

@Module
@InstallIn(ActivityRetainedComponent::class)
class MediaPlayerModule {

    @Provides
    fun provideMediaPlayer(): MediaPlayer = MediaPlayer()

}