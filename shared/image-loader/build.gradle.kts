plugins {
    id("krypt.android.library")
}

android {
    namespace = "ir.mehdiyari.krypt.imageloader"
}

dependencies {
    implementation(libs.coreKtx)
    implementation(libs.androidx.appCompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.testExt)
    androidTestImplementation(libs.espressoCore)
}