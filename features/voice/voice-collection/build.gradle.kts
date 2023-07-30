plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.voice.collection"
}

dependencies {

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)
}
