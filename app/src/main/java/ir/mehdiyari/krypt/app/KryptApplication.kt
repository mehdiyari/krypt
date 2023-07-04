package ir.mehdiyari.krypt.app

import android.app.Application
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KryptApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (ProcessPhoenix.isPhoenixProcess(this)) {
            return
        }
    }
}