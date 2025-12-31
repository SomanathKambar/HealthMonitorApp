plugins {
    id("vkm.android.library")
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.vkm.healthmonitor.core.model"
}

dependencies {
    implementation(libs.androidx.room.runtime)
}
