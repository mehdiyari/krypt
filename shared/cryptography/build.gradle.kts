plugins {
    id("krypt.android.library")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.cryptography"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

}

dependencies {

    implementation(libs.coroutinesAndroid)
    implementation(libs.coroutinesCore)

    implementation(libs.coreKtx)
    androidTestImplementation(libs.testExt)
    androidTestImplementation(libs.espressoCore)

    implementation(project(":shared:dispatchers"))
}