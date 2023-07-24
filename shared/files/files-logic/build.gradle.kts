plugins {
    id("krypt.android.library")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.files.logic"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(libs.coroutinesAndroid)
    implementation(libs.coroutinesCore)
    implementation(libs.coreKtx)
    implementation(libs.androidx.documentfile)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    androidTestImplementation(libs.testExt)

    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:dispatchers"))
    implementation(project(":shared:accounts:account"))
    implementation(project(":shared:files:backup-data")) // TODO: we should remove this dependency
}