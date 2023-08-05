package ir.mehdiyari.krypt.voice.player.di

import android.media.MediaPlayer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import ir.mehdiyari.krypt.voice.player.api.KryptMediaPlayer
import ir.mehdiyari.krypt.voice.player.impl.KryptMediaPlayerImpl

@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class MediaPlayerBindingModule {

    @Binds
    abstract fun bindKryptMusicPlayer(
        kryptMusicPlayerImpl: KryptMediaPlayerImpl
    ): KryptMediaPlayer

}

@Module
@InstallIn(ActivityRetainedComponent::class)
internal class MediaPlayerModule {

    @Provides
    fun provideMediaPlayer(): MediaPlayer = MediaPlayer()

}