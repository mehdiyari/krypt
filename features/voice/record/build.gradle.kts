plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.voice.record"
}

dependencies {

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)
}