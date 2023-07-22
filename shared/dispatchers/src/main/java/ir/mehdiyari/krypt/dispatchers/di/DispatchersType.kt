package ir.mehdiyari.krypt.dispatchers.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatchersType(val dispatcher: DispatchersQualifierType)

