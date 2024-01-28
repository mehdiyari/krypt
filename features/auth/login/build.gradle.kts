plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.features.auth.login"
}

dependencies {

    implementation(project(":shared:designsystem:components"))
    implementation(project(":shared:designsystem:resources"))

    implementation(project(":shared:accounts:account"))

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)

}