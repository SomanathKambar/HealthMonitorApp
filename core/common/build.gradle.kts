plugins {
    id("vkm.android.library")
}

android {
    namespace = "com.vkm.healthmonitor.core.common"
}

dependencies {
    implementation(project(":core:model"))
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.firebase.firestore)
}
