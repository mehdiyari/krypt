package ir.mehdiyari.krypt.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.data.repositories.settings.SettingsRepository
import ir.mehdiyari.krypt.data.repositories.settings.SettingsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {
    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}