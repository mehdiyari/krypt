package ir.mehdiyari.krypt.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.data.repositories.settings.SettingsRepository
import ir.mehdiyari.krypt.data.repositories.settings.DefaultSettingsRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindSettingsRepository(impl: DefaultSettingsRepository): SettingsRepository
}