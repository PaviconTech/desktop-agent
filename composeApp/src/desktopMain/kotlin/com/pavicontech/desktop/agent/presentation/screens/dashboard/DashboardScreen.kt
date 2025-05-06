package com.pavicontech.desktop.agent.presentation.screens.dashboard

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.pavicontech.desktop.agent.presentation.navigation.graphs.dashboardGraph
import com.pavicontech.desktop.agent.presentation.navigation.screens.DashboardScreens
import com.pavicontech.desktop.agent.presentation.navigation.screens.Graphs
import com.pavicontech.printer.presentation.screens.dashboard.components.DashboardLayout
import com.pavicontech.printer.presentation.screens.dashboard.components.SidePanel

@Composable
fun DashboardScreen(){
    val navController = rememberNavController()

    DashboardLayout(
        sidePanel = {
            SidePanel(
                onNavigateToHome = { navController.navigate(DashboardScreens.HomeScreen){navController.popBackStack()} },
                onNavigateToSettings = { navController.navigate(DashboardScreens.SettingsScreen){navController.popBackStack()} }
            )
        },
        mainPanel = {
            Surface(
                color = MaterialTheme.colors.surface.copy(0.3f)
            ){
                NavHost(
                    navController = navController,
                    startDestination = Graphs.DashboardGraph,
                ) {
                    dashboardGraph(navController = navController)
                }
            }
        }

    )

}