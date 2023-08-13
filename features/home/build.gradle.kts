plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.home"
}

dependencies {
    implementation(project(":shared:designsystem:components"))
    implementation(project(":shared:designsystem:resources"))

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)

}