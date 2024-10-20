plugins {
    id("krypt.android.library")
    id("krypt.android.library.compose")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.restore"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":shared:designsystem:theme"))
    implementation(project(":shared:designsystem:components"))
    implementation(project(":shared:designsystem:resources"))
    implementation(project(":shared:cryptography"))

    implementation(project(":shared:dispatchers"))
    implementation(project(":shared:files:backup-logic"))
    implementation(project(":shared:files:backup-data"))
    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:permissions"))

    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.coroutinesAndroid)
    implementation(libs.coroutinesCore)

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)

}