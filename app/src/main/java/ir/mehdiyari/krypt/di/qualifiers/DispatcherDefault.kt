package ir.mehdiyari.krypt.di.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatcherDefault(val name: String = "default_dispatcher")
