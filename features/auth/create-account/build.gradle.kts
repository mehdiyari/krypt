plugins {
   id("krypt.android.library")
   id("krypt.android.library.compose")
   id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.createAccount"

    defaultConfig {

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

    implementation(project(":shared:designsystem:theme"))
    implementation(project(":shared:designsystem:resources"))
    implementation(project(":shared:designsystem:components"))

    implementation(project(":shared:dispatchers"))
    implementation(project(":shared:accounts:account"))

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)

}