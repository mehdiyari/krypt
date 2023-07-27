@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("krypt.android.library")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.backup.logic"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

}

dependencies {

    implementation(project(":shared:files:backup-data"))
    implementation(project(":shared:accounts:account-data"))
    implementation(project(":shared:accounts:account"))
    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:cryptography"))

    implementation(libs.coroutinesCore)
    implementation(libs.coreKtx)
    implementation(libs.moshiKotlin)
    testImplementation(libs.junit)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.testExt)
    androidTestImplementation(libs.espressoCore)
}