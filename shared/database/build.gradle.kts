plugins {
    id("krypt.android.library")
    id("krypt.android.hilt")
    alias(libs.plugins.kspPlugin)
}

android {
    namespace = "ir.mehdiyari.krypt.database"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(libs.room)
    ksp(libs.roomCompiler)

    implementation(libs.coreKtx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.testExt)
    androidTestImplementation(libs.espressoCore)

    implementation(project(":shared:accounts:account-data"))
    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:files:backup-data"))
}