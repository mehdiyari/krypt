package ir.mehdiyari.krypt.setting.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.setting.data.repositories.SettingsRepository
import ir.mehdiyari.krypt.setting.data.repositories.SettingsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SettingsBindingModule {
    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}