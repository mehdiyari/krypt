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
include(":shared:designsystem:resources")
include(":features:auth:login")
include(":shared:files:files-logic")
include(":features:settings")
include(":shared:files:backup-logic")
include(":features:auth:create-account")
include(":features:backup")
include(":shared:permissions")
include(":features:voice:record")
include(":features:voice:player")
include(":features:voice:voice-collection")
include(":features:voice:shared")
