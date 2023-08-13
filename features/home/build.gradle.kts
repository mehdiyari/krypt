plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.home"
}

dependencies {
    implementation(project(":shared:designsystem:components"))
    implementation(project(":shared:designsystem:resources"))

    implementation(project(":shared:dispatchers"))
    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:accounts:account"))
    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:share-content"))
    implementation(project(":features:media:list"))

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)

}