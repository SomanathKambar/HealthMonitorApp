plugins {
    id("vkm.android.library")
}

android {
    namespace = "com.vkm.healthmonitor.core.common"
}

dependencies {
    implementation(project(":core:model"))
    implementation(platform(libs.firebase.bom))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.biometric)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore)
}
