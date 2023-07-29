plugins {
    id("krypt.android.library")
    id("krypt.android.library.compose")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.textsList"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

}