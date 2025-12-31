package com.vkm.healthmonitor.compose.ui.screens

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vkm.healthmonitor.R
import com.vkm.healthmonitor.core.common.Screen

data class BottomNavItem(val route: String, val label: String, val icon: Int)

val bottomItems = listOf(
    BottomNavItem(Screen.Profile.route, "Profile", R.drawable.ic_profile),
    BottomNavItem(Screen.Vitals.route, "Vitals", R.drawable.ic_vitals),
    BottomNavItem(Screen.Hydration.route, "Hydration", R.drawable.ic_water),
    BottomNavItem(Screen.Charts.route, "Charts", R.drawable.ic_chart),
    BottomNavItem(Screen.Guides.route, "Guides", R.drawable.ic_guide)
)

@Composable
fun BottomBar(navController: NavController) {
    val backStack = navController.currentBackStackEntryAsState()
    NavigationBar {
        bottomItems.forEach { item ->
            val selected = backStack.value?.destination?.route == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(item.route) { popUpTo(Screen.Profile.route); launchSingleTop = true } },
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
