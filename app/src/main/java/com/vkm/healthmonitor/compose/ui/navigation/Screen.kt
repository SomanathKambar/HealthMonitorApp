package com.vkm.healthmonitor.compose.ui.navigation

sealed class Screen(val route: String) {
    object ProfileList : Screen("profileList")

    object Profile : Screen("profile")
    object ProfileForm : Screen("profileForm")
    object Vitals : Screen("vitals")
    object Hydration : Screen("hydration")
    object Charts : Screen("charts")
    object Guides : Screen("guides")
}
