package ir.mehdiyari.krypt

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

fun Project.configureComposeAndroid(extension: CommonExtension<*, *, *, *, *, *>) {
    with(extension) {
        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion =
                libs.findVersion("androidxComposeCompiler").get().toString()
        }
    }
}