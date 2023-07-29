plugins {
    id("krypt.android.library")
    id("krypt.android.library.compose")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.textsList"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

    implementation(project(":shared:designsystem:theme"))
    implementation(project(":shared:designsystem:resources"))

    implementation(project(":shared:dispatchers"))
    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:files:files-data"))
    implementation(project(":features:text:shared"))

    implementation(libs.androidx.lifecycle.viewmodelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.hilt.navigation.compose)
}