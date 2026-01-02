package com.vkm.healthmonitor.compose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vkm.healthmonitor.core.common.Screen
import com.vkm.healthmonitor.compose.ui.screens.ChartsScreen
import com.vkm.healthmonitor.compose.ui.screens.GuidesScreen
import com.vkm.healthmonitor.feature.hydration.HydrationScreen
import com.vkm.healthmonitor.feature.profile.ProfileFormScreen
import com.vkm.healthmonitor.feature.profile.ProfileListScreen
import com.vkm.healthmonitor.feature.home.EnergyDashboardScreen
import com.vkm.healthmonitor.feature.home.LightProtocolScreen
import com.vkm.healthmonitor.compose.ui.screens.VitalsScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box

@Composable
fun MainNavHost(navController: NavHostController, paddingValues: PaddingValues) {
    NavHost(navController = navController, startDestination = Screen.EnergyDashboard.route) {
        composable(Screen.EnergyDashboard.route) { 
            EnergyDashboardScreen(
                paddingValues = paddingValues,
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToHydration = { navController.navigate(Screen.Hydration.route) },
                onNavigateToLightProtocol = { navController.navigate(Screen.LightProtocol.route) }
            ) 
        }
        composable(Screen.LightProtocol.route) {
            LightProtocolScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) { ProfileFormScreen(navController, paddingValues = paddingValues) }
        composable(Screen.ProfileForm.route) { ProfileFormScreen(navController, paddingValues = paddingValues) }
        composable(Screen.Vitals.route) { VitalsScreen() }
        composable(Screen.Hydration.route) { HydrationScreen(paddingValues = paddingValues) }
        composable(Screen.Charts.route) { ChartsScreen(paddingValues = paddingValues) }
        composable(Screen.Guides.route) { GuidesScreen(navController) }
    }
}
