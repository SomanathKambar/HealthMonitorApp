plugins {
    id("vkm.android.library")
}

android {
    namespace = "com.vkm.healthmonitor.core.datastore"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
}
