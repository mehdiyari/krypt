plugins {
    id("krypt.android.library")
    id("krypt.android.library.compose")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.features.auth.login"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

    implementation(project(":shared:designsystem:theme"))
    implementation(project(":shared:designsystem:components"))
    implementation(project(":shared:designsystem:resources"))

    implementation(project(":shared:dispatchers"))
    implementation(project(":shared:accounts:account"))

    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.hilt.navigation.compose)

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)

}