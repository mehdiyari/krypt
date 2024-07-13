// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // TODO MHD: check class path
        classpath(libs.hiltAndroidGradleClassPath)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kspPlugin) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}