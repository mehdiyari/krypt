package ir.mehdiyari.krypt

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.applyAndroidApplicationPlugin(){
    pluginManager.apply(libs.findPlugin("android.application").get().get().pluginId)
}

fun Project.applyAndroidLibraryPlugin(){
    pluginManager.apply(libs.findPlugin("android.library").get().get().pluginId)
}

fun Project.applyKotlinAndroidPlugin(){
    pluginManager.apply(libs.findPlugin("kotlin.android").get().get().pluginId)
}
