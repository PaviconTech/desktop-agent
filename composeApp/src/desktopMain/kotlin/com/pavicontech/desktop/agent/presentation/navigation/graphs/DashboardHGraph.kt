package com.pavicontech.desktop.agent.presentation.navigation.graphs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.pavicontech.desktop.agent.presentation.navigation.screens.DashboardScreens
import com.pavicontech.desktop.agent.presentation.navigation.screens.Graphs
import com.pavicontech.desktop.agent.presentation.screens.dashboard.items.ItemsScreen
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.ProfileScreen
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.creditNote.CreditNoteScreenScreen
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.SalesScreen
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.SettingsScreen
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.status.StatusScreen


fun NavGraphBuilder.dashboardGraph(navController: NavHostController) {
    navigation<Graphs.DashboardGraph>(
        startDestination = DashboardScreens.Status,
    ) {

        composable<DashboardScreens.Status>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) {
            StatusScreen(
                onNavigateToSettings = {navController.navigate(DashboardScreens.SettingsScreen) { navController.popBackStack() } }
            )
        }

        composable<DashboardScreens.Sales>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) { SalesScreen() }

        composable<DashboardScreens.Items>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) {
            ItemsScreen()
        }

        composable<DashboardScreens.CreditNotes>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) { CreditNoteScreenScreen() }

        composable<DashboardScreens.Customers>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) { Text("Customers") }

        composable<DashboardScreens.Purchases>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) {Text("Purchases") }

        composable<DashboardScreens.Imports>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) {Text("Imports") }

        composable<DashboardScreens.Insurance>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) {Text("Insurance") }

        composable<DashboardScreens.Reports>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) {Text("Reports") }

        composable<DashboardScreens.SettingsScreen>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) { SettingsScreen() }

        composable<DashboardScreens.ProfileScreen>(
            enterTransition = { slideInVertically( initialOffsetY = { it }, animationSpec = tween(600, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutVertically( targetOffsetY = { -it }, animationSpec = tween(600, easing = LinearOutSlowInEasing)) }
        ) { ProfileScreen(onBack = { navController.navigate(DashboardScreens.Sales) { navController.popBackStack() } }) }
    }
}