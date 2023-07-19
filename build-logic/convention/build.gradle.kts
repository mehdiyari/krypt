plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {

    plugins {

        register("kryptAndroidApplication"){
            id = "krypt.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("kryptAndroidLibrary"){
            id = "krypt.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("kryptAndroidLibraryCompose"){
            id = "krypt.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

    }

}