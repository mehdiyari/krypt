plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.mediaList"
}

dependencies {
    implementation(project(":shared:designsystem:resources"))
    implementation(project(":shared:designsystem:components"))
    implementation(project(":shared:cryptography"))
    implementation(project(":shared:permissions"))
    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:share-content"))

    implementation(project(":features:media:player")) //FIXME MHD: features must not see each other

    implementation(libs.fallery)
    implementation(libs.glide)
    implementation(libs.landscapistGlide)

    testImplementation(libs.mockk)
    testImplementation(libs.coroutinesTest)
    testImplementation(libs.turbine)
    testImplementation(libs.junit)

}