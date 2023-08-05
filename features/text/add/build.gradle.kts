plugins {
    id("krypt.android.feature")
}

android {
    namespace = "ir.mehdiyari.krypt.addText"
}

dependencies {

    implementation(project(":shared:designsystem:resources"))

    implementation(project(":features:text:shared"))

    implementation(project(":shared:files:files-logic"))
    implementation(project(":shared:files:files-data"))
    implementation(project(":shared:files:files-data"))
}