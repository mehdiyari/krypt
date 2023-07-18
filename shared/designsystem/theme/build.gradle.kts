plugins {
    id("krypt.android.library")
    id("krypt.android.library.compose")
}

android {
    namespace = "ir.mehdiyari.krypt.core.designsystem.theme"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    api(composeBom)
    androidTestApi(composeBom)

    api(libs.androidx.compose.material3)

    // Android Studio Preview support
    api(libs.androidx.compose.ui.tooling.preview)
    debugApi(libs.androidx.compose.ui.tooling)

    // UI Tests
    androidTestApi(libs.androidx.compose.ui.test)
    debugApi(libs.androidx.compose.ui.testManifest)

//    api(libs.androidx.test.runner)
}