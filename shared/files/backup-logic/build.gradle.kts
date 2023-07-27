@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("krypt.android.library")
    id("krypt.android.hilt")
    alias(libs.plugins.kspPlugin)
}

android {
    namespace = "ir.mehdiyari.krypt.backup.logic"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

}

dependencies {
    implementation(libs.coroutinesCore)
    implementation(libs.room)
    ksp(libs.room)
    implementation(libs.coreKtx)
    testImplementation(libs.junit)
    testImplementation(libs.coroutinesTest)
    androidTestImplementation(libs.testExt)
    androidTestImplementation(libs.espressoCore)
}