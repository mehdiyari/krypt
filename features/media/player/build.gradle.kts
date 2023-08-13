plugins {
    id("krypt.android.feature")
    id("dagger.hilt.android.plugin") //We need this dependency because we used AndroidEntrypoint annotation
}

android {
    namespace = "ir.mehdiyari.krypt.mediaPlayer"
}

dependencies {

    implementation(libs.androidx.appCompat)
    implementation(libs.exoplayer)
    implementation(libs.media3UI)
    implementation(project(":shared:cryptography"))
    implementation(project(":shared:files:files-logic"))

}