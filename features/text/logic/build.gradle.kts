plugins {
    id("krypt.android.library")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.textLogic"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

    implementation(project(":shared:cryptography"))
    implementation(project(":shared:files:files-logic"))
}