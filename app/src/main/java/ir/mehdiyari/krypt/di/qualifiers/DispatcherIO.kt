package ir.mehdiyari.krypt.di.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatcherIO(val name:String = "io_dispatcher")
