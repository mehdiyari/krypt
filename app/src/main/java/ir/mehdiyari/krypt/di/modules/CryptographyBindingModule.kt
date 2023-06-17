package ir.mehdiyari.krypt.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mehdiyari.krypt.crypto.KryptCryptographyHelper
import ir.mehdiyari.krypt.crypto.KryptCryptographyHelperImpl
import ir.mehdiyari.krypt.crypto.api.ByteCryptography
import ir.mehdiyari.krypt.crypto.api.FileCryptography
import ir.mehdiyari.krypt.crypto.impl.ByteCryptographyImpl
import ir.mehdiyari.krypt.crypto.impl.FileCryptographyImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class CryptographyBindingModule {

    @Binds
    abstract fun bindByteCryptography(
        byteCryptographyImpl: ByteCryptographyImpl
    ): ByteCryptography

    @Binds
    abstract fun bindFileCryptography(
        fileCryptographyImpl: FileCryptographyImpl
    ): FileCryptography

    @Binds
    abstract fun bindKryptCryptographyHelper(
        kryptCryptographyHelperImpl: KryptCryptographyHelperImpl
    ): KryptCryptographyHelper
}