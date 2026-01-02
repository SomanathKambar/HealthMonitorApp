package com.vkm.healthmonitor.compose.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.vkm.healthmonitor.compose.ui.navigation.MainNavHost

@Composable
fun MainAppScaffold() {
    val nav = rememberNavController()
    Scaffold (
        bottomBar = { BottomBar(nav) }
    ) { innerPadding ->
        MainNavHost(nav, innerPadding)
    }
}
