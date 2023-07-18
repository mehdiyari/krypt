package ir.mehdiyari.krypt.cryptography.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.cryptography.api.ByteCryptography
import ir.mehdiyari.krypt.cryptography.api.FileCryptography
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.cryptography.api.KryptKeyGenerator
import ir.mehdiyari.krypt.cryptography.impl.ByteCryptographyImpl
import ir.mehdiyari.krypt.cryptography.impl.FileCryptographyImpl
import ir.mehdiyari.krypt.cryptography.impl.KryptCryptographyHelperImpl
import ir.mehdiyari.krypt.cryptography.impl.KryptKeyGeneratorImpl

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CryptographyBindingModule {

    @Binds
    abstract fun bindByteCryptography(
        byteCryptographyImpl: ByteCryptographyImpl
    ): ByteCryptography

    @Binds
    abstract fun bindFileCryptography(
        fileCryptographyImpl: FileCryptographyImpl
    ): FileCryptography

    @Binds
    abstract fun bindKryptKeyGenerator(
        kryptKeyGeneratorImpl: KryptKeyGeneratorImpl
    ): KryptKeyGenerator

    @Binds
    abstract fun bindKryptCryptographyHelper(
        kryptCryptographyHelperImpl: KryptCryptographyHelperImpl
    ): KryptCryptographyHelper
}