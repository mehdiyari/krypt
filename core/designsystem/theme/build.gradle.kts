@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibraryPlugin)
    alias(libs.plugins.kotlinAndroidPlugin)
}

android {
    namespace = "ir.mehdiyari.krypt.core.designsystem.theme"
    compileSdk = 33

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
}

dependencies {
    val composeBom = platform(libs.composeBom)
    api(composeBom)
    debugApi(composeBom)
    api(libs.composeActivity)
    api(libs.composeAnimation)
    api(libs.composeUiTooling)
    api(libs.material3)
    androidTestApi(libs.composeJunit4UiTest)

}