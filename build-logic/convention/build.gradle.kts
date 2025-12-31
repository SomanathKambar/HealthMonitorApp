plugins {
    `kotlin-dsl`
}

group = "com.vkm.healthmonitor.buildlogic"

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "vkm.android.application"
            implementationClass = "com.vkm.healthmonitor.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "vkm.android.library"
            implementationClass = "com.vkm.healthmonitor.buildlogic.AndroidLibraryConventionPlugin"
        }
    }
}
