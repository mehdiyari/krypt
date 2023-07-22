plugins {
    id 'krypt.android.library'
    id 'krypt.android.hilt'
}

android {
    namespace 'ir.mehdiyari.krypt.account'

    defaultConfig {
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation libs.room
    implementation libs.coreKtx
    testImplementation libs.junit
    androidTestImplementation libs.testExt

    implementation project(":shared:cryptography")
    implementation project(":shared:accounts:account-data")
}