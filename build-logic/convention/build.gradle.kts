plugins {
    `kotlin-dsl`
}

group = "com.vkm.healthmonitor.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
}