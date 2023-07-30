plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.textsList"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

    implementation(project(":shared:designsystem:resources"))

    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:files:files-data"))

    implementation(project(":features:text:shared"))
}