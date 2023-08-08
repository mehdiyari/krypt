plugins {
    id("krypt.android.library")
    id("krypt.android.library.compose")
}

android {
    namespace = "ir.mehdiyari.krypt.designsystem.theme"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    api(composeBom)
    androidTestApi(composeBom)

    api(libs.androidx.compose.material3)
    api(libs.androidx.constraintLayout.compose) //TODO MHD: Investigate if it's possible to avoid constraint layout

    // Android Studio Preview support
    api(libs.androidx.compose.ui.tooling.preview)
    debugApi(libs.androidx.compose.ui.tooling)

    // UI Tests
    androidTestApi(libs.androidx.compose.ui.test)
    debugApi(libs.androidx.compose.ui.testManifest)

    //We need this dependency to use M2 themes in themes.xml
    implementation(libs.material)

}