plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.voice.collection"
}

dependencies {
    implementation(project(":shared:designsystem:theme"))
    implementation(project(":shared:designsystem:resources"))

    implementation(project(":features:voice:shared"))
    implementation(project(":features:voice:player"))
    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:files:files-logic"))

    implementation(libs.moshiKotlin)

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)
}
