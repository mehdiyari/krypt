plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.setting"
}

dependencies {
    implementation(project(":shared:designsystem:components"))
    implementation(project(":shared:designsystem:resources"))

    implementation(project(":shared:accounts:account"))
    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:files:files-data"))
    testImplementation(project(":shared:testing"))
}