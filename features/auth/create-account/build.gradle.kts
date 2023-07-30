plugins {
   id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.createAccount"
}

dependencies {

    implementation(project(":shared:designsystem:resources"))
    implementation(project(":shared:designsystem:components"))

    implementation(project(":shared:accounts:account"))

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)

}