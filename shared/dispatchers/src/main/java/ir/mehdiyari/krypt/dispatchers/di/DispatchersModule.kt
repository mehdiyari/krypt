package ir.mehdiyari.krypt.dispatchers.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
class DispatchersModule {

    @Provides
    @DispatchersType(DispatchersQualifierType.IO)
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DispatchersType(DispatchersQualifierType.DEFAULT)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @DispatchersType(DispatchersQualifierType.MAIN)
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

}