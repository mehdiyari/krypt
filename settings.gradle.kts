pluginManagement {
    repositories {
        includeBuild("build-logic")
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")

    }
}
rootProject.name = "Krypt"
include(":app")
include(":shared:cryptography")
include(":shared:dispatchers")
include(":shared:designsystem:theme")
