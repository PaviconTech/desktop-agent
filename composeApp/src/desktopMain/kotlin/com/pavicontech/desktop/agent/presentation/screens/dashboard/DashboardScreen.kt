package com.pavicontech.desktop.agent.presentation.screens.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
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
        navBar = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.secondary.copy(0.1f)
            ) {
                NavBar(
                    profileName = viewModel.businessProfile?.taxpayerName ?: "Avatar",
                    branch = viewModel.businessProfile?.branchName ?: "Branch",
                    kraPin = viewModel.businessProfile?.kraPin ?: "",
                    onViewProfile = {
                        navController.navigate(
                            DashboardScreens.ProfileScreen(
                                id = viewModel.businessProfile?.id ?: 0,
                                name = viewModel.businessProfile?.name ?: "",
                                branchId = viewModel.businessProfile?.branchId ?: "",
                                branchName = viewModel.businessProfile?.branchName ?: "",
                                districtName = viewModel.businessProfile?.districtName ?: "",
                                kraPin = viewModel.businessProfile?.kraPin ?: "",
                                provinceName = viewModel.businessProfile?.provinceName ?: "",
                                sectorName = viewModel.businessProfile?.sectorName ?: "",
                                sdcId = viewModel.businessProfile?.sdcId ?: "",
                                taxpayerName = viewModel.businessProfile?.taxpayerName ?: ""
                            )
                        ){
                            navController.popBackStack()
                        }
                    }
                )
            }
        },
        sidePanel = {
            SidePanel(
                onNavigateToHome = { navController.navigate(DashboardScreens.HomeScreen) { navController.popBackStack() } },
                onNavigateToSettings = { navController.navigate(DashboardScreens.SettingsScreen) { navController.popBackStack() } },
                onLogOut = {
                    viewModel.logout()
                    onNavigateToAuth()
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