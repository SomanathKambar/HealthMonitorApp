plugins {
    id("vkm.android.library")
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.vkm.healthmonitor.core.database"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
}
