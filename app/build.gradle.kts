import ir.mehdiyari.krypt.Versions

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("krypt.android.application")
    id("krypt.android.application.flavor")
    id("krypt.android.application.compose")
    alias(libs.plugins.kspPlugin)
    id("dagger.hilt.android.plugin")
    id("krypt.android.hilt")
}

android {

    defaultConfig {

        applicationId = "ir.mehdiyari.krypt"
        versionCode = Versions.VERSION_CODE
        versionName = Versions.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    namespace = "ir.mehdiyari.krypt"
}

dependencies {
    implementation(libs.coreKtx)
    implementation(libs.androidx.appCompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.testExt)
    androidTestImplementation(libs.espressoCore)

    api(libs.composeActivity)

    implementation(libs.lifecycleViewModeKtx)
    implementation(libs.androidx.lifecycle.viewmodelCompose)
    ksp(libs.lifecycleCompiler)

    implementation(libs.room)
    ksp(libs.roomCompiler)

    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.navigationCompose)

    implementation(libs.coroutinesAndroid)
    implementation(libs.coroutinesCore)

    ksp(libs.glideCompiler)

    implementation(libs.moshiKotlin)
    implementation(libs.processPhoenix)

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.turbine)

    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.splashscreen)

    implementation(project(":shared:dispatchers"))
    implementation(project(":shared:cryptography"))
    implementation(project(":shared:permissions"))
    implementation(project(":shared:accounts:account"))
    implementation(project(":shared:database"))
    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:files:backup-data"))
    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:files:backup-logic"))
    implementation(project(":shared:accounts:account-data"))
    implementation(project(":shared:designsystem:theme"))
    implementation(project(":shared:designsystem:components"))
    implementation(project(":shared:designsystem:resources"))
    implementation(project(":shared:share-content"))

    implementation(project(":features:auth:login"))
    implementation(project(":features:auth:create-account"))
    implementation(project(":features:settings"))
    implementation(project(":features:backup"))
    implementation(project(":features:text:list"))
    implementation(project(":features:text:add"))
    implementation(project(":features:media:player"))
    implementation(project(":features:media:list"))

    implementation(project(":features:voice:player"))
    implementation(project(":features:voice:shared"))
    implementation(project(":features:voice:record"))
    implementation(project(":features:voice:voice-collection"))

    implementation(project(":features:home"))
    implementation(project(":features:restore"))
}