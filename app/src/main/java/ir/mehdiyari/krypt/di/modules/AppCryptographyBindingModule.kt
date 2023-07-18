package ir.mehdiyari.krypt.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.crypto.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.crypto.impl.KryptCryptographyHelperImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class AppCryptographyBindingModule {

    @Binds
    abstract fun bindKryptCryptographyHelper(
        kryptCryptographyHelperImpl: KryptCryptographyHelperImpl
    ): KryptCryptographyHelper

}