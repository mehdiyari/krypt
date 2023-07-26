plugins {
    id("krypt.android.library")
    id("krypt.android.library.compose")
}

android {
    namespace = "ir.mehdiyari.krypt.shared.designsystem.components"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

}

dependencies {
    implementation(project(":shared:designsystem:theme"))
}