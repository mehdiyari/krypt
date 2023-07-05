package ir.mehdiyari.krypt.app.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SplashDelay(val name: String = "splash_delay")