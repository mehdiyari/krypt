plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.voice.shared"
}

dependencies {
    implementation(libs.moshiKotlin)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)
}
