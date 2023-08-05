plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.voice.record"
}

dependencies {
    implementation(project(":shared:designsystem:theme"))
    implementation(project(":shared:designsystem:components"))
    implementation(project(":shared:designsystem:resources"))

    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:cryptography"))
    implementation(project(":features:voice:shared"))

    implementation(libs.moshiKotlin)

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)
}