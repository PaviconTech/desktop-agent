package com.pavicontech.desktop.agent.presentation.navigation.graphs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.pavicontech.desktop.agent.presentation.navigation.screens.DashboardScreens
import com.pavicontech.desktop.agent.presentation.navigation.screens.Graphs
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.home.HomeScreen
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.SettingsScreen


fun NavGraphBuilder.dashboardGraph(navController: NavHostController) {
    navigation<Graphs.DashboardGraph>(
        startDestination = DashboardScreens.HomeScreen,
    ) {

        composable<DashboardScreens.HomeScreen>(
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(600, easing = LinearOutSlowInEasing)
                )
            }
        ) {
            HomeScreen()
        }

        composable<DashboardScreens.SettingsScreen>(
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(600, easing = LinearOutSlowInEasing)
                )
            }
        ) { SettingsScreen() }
    }
}