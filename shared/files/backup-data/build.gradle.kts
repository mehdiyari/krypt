plugins {
    id("krypt.android.library")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.backup.data"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

}

dependencies {
    implementation(libs.room)
    implementation(libs.coreKtx)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.turbine)
    testImplementation(libs.junit)
    androidTestImplementation(libs.testExt)
    androidTestImplementation(libs.espressoCore)
}