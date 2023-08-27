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

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    api(libs.mockk)
    api(libs.coroutinesTest)
    api(libs.turbine)
    api(libs.junit)
}