package ir.mehdiyari.krypt.di.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AccountName(val name:String = "account_name")
