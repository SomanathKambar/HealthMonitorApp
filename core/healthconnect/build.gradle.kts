plugins {
    id("vkm.android.library")
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.vkm.healthmonitor.core.healthconnect"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.health.connect.client)
    
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
}
