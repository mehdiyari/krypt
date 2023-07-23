plugins {
    id 'krypt.android.library'
    id 'krypt.android.hilt'
    alias(libs.plugins.kspPlugin)
}

android {
    namespace 'ir.mehdiyari.krypt.accounts.data'
    defaultConfig {
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

}

dependencies {
    implementation libs.room
    implementation libs.coreKtx
    testImplementation libs.junit
    androidTestImplementation libs.testExt
    androidTestImplementation libs.espressoCore
}