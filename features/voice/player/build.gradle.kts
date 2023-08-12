plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.voice.player"
}

dependencies {
    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:cryptography"))
    implementation(project(":features:voice:shared"))
    implementation(libs.moshiKotlin)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)
}
