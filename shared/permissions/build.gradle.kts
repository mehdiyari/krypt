plugins {
    id("krypt.android.library")
}

android {
    namespace = "ir.mehdiyari.krypt.permission"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)
}