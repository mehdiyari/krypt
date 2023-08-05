plugins {
    id("krypt.android.library")
    id("krypt.android.hilt")
}

android {
    namespace = "ir.mehdiyari.krypt.shareContent"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

    implementation(libs.androidx.lifecycle.viewmodelCompose)

}