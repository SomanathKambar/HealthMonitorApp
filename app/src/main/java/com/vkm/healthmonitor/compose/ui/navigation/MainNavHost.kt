package com.vkm.healthmonitor.compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vkm.healthmonitor.compose.ui.screens.ChartsScreen
import com.vkm.healthmonitor.compose.ui.screens.GuidesScreen
import com.vkm.healthmonitor.compose.ui.screens.HydrationScreen
import com.vkm.healthmonitor.compose.ui.screens.ProfileFormScreen
import com.vkm.healthmonitor.compose.ui.screens.ProfileListScreen
import com.vkm.healthmonitor.compose.ui.screens.VitalsScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.ProfileList.route) {
        composable(Screen.ProfileList.route) { ProfileListScreen(navController) }
        composable(Screen.Profile.route) { ProfileListScreen(navController) }
        composable(Screen.ProfileForm.route) { ProfileFormScreen(navController) }
        composable(Screen.Vitals.route) { VitalsScreen() }
        composable(Screen.Hydration.route) { HydrationScreen() }
        composable(Screen.Charts.route) { ChartsScreen() }
        composable(Screen.Guides.route) { GuidesScreen(navController) }
    }
}
