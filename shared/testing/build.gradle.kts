plugins {
    id("krypt.android.library")
}

android {
    namespace = "ir.mehdiyari.krypt.testing"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}


dependencies {
    api(libs.mockk)
    api(libs.coroutinesTest)
    api(libs.turbine)
    api(libs.junit)
}