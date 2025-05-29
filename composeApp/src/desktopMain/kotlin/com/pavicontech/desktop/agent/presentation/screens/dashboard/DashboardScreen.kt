package com.pavicontech.desktop.agent.presentation.screens.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pavicontech.desktop.agent.presentation.navigation.graphs.dashboardGraph
import com.pavicontech.desktop.agent.presentation.navigation.screens.DashboardScreens
import com.pavicontech.desktop.agent.presentation.navigation.screens.Graphs
import com.pavicontech.desktop.agent.presentation.screens.dashboard.components.NavBar
import com.pavicontech.desktop.agent.presentation.screens.dashboard.components.ProfileDialogBox
import com.pavicontech.desktop.agent.presentation.screens.dashboard.components.DashboardLayout
import com.pavicontech.desktop.agent.presentation.screens.dashboard.components.SidePanel
import org.koin.compose.koinInject

@Composable
fun DashboardScreen(
    onNavigateToAuth: () -> Unit
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute by remember {
        derivedStateOf {
            when (currentBackStackEntry?.destination?.route) {
                DashboardScreens.Sales::class.qualifiedName -> DashboardScreens.Sales
                DashboardScreens.Status::class.qualifiedName -> DashboardScreens.Status
                DashboardScreens.Items::class.qualifiedName -> DashboardScreens.Items
                DashboardScreens.CreditNotes::class.qualifiedName -> DashboardScreens.CreditNotes
                DashboardScreens.Customers::class.qualifiedName ->DashboardScreens.Customers
                DashboardScreens.Purchases::class.qualifiedName -> DashboardScreens.Purchases
                DashboardScreens.Imports::class.qualifiedName -> DashboardScreens.Imports
                DashboardScreens.Insurance::class.qualifiedName -> DashboardScreens.Insurance
                DashboardScreens.Reports::class.qualifiedName -> DashboardScreens.Reports
                DashboardScreens.SettingsScreen::class.qualifiedName -> DashboardScreens.SettingsScreen
                else -> DashboardScreens.Sales
            }
        }
    }

    val viewModel: DashboardViewModel = koinInject()
    var isBusinessDialogOpen by remember { mutableStateOf(false) }

    ProfileDialogBox(
        isDialogVisible = isBusinessDialogOpen,
        profile = viewModel.businessProfile,
        onDismiss = {
            isBusinessDialogOpen = false
        }
    )

    DashboardLayout(
        sidePanel = {
            SidePanel(
                currentRoute = currentRoute,
                onNavigateToStatus = { navController.navigate(DashboardScreens.Status) { navController.popBackStack() } },
                onNavigateToHome = { navController.navigate(DashboardScreens.Sales) { navController.popBackStack() } },
                onNavigateToItems = { navController.navigate(DashboardScreens.Items) { navController.popBackStack() } },
                onNavigateToCreditNotes = { navController.navigate(DashboardScreens.CreditNotes) { navController.popBackStack() } },
                onNavigateToCustomers = { navController.navigate(DashboardScreens.Customers) { navController.popBackStack() } },
                onNavigateToPurchases = { navController.navigate(DashboardScreens.Purchases) { navController.popBackStack() } },
                onNavigateToImports = { navController.navigate(DashboardScreens.Imports) { navController.popBackStack() } },
                onNavigateToReports = { navController.navigate(DashboardScreens.Reports) { navController.popBackStack() } },
                onNavigateToSettings = { navController.navigate(DashboardScreens.SettingsScreen) { navController.popBackStack() } },
                onLogOut = {
                    viewModel.logout()
                    onNavigateToAuth()
                },
                profileName = viewModel.businessProfile?.name ?: "KRA",
                onViewProfile = {
                    navController.navigate(DashboardScreens.ProfileScreen()){navController.popBackStack()}
                }
            )
        },
        mainPanel = {
            Surface(
                color = MaterialTheme.colors.background
            ) {
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