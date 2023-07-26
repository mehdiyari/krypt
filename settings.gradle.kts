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
include(":shared:accounts:account")
include(":shared:database")
include(":shared:files:files-data")
include(":shared:files:backup-data")
include(":shared:accounts:account-data")
include(":shared:designsystem:components")
