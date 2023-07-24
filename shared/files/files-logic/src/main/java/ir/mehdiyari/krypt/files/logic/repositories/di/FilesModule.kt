package ir.mehdiyari.krypt.files.logic.repositories.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.files.logic.repositories.api.FilesRepository
import ir.mehdiyari.krypt.files.logic.repositories.impl.FilesRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FilesBindingModule {

    @Binds
    abstract fun bindFilesRepository(impl: FilesRepositoryImpl): FilesRepository

}