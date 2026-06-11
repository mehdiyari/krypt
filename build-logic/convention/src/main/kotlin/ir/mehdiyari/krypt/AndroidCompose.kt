package ir.mehdiyari.krypt

import com.android.build.api.dsl.CommonExtension

fun configureComposeAndroid(extension: CommonExtension) {
    extension.buildFeatures.compose = true
}