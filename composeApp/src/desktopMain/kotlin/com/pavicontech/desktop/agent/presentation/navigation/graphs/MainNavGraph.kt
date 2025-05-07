package com.pavicontech.desktop.agent.presentation.navigation.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pavicontech.desktop.agent.presentation.navigation.screens.AuthScreens
import com.pavicontech.desktop.agent.presentation.navigation.screens.Graphs
import com.pavicontech.desktop.agent.presentation.screens.dashboard.DashboardScreen
import io.ktor.client.plugins.auth.Auth


@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Graphs.OnboardingGraph,
    ) {
        onboardingGraph(navController)
        authGraphs(navController)
        composable<Graphs.DashboardGraph> {
            DashboardScreen(
                onNavigateToAuth = {
                    navController.navigate(AuthScreens.SignIn){
                        popUpTo<AuthScreens.SignIn>{inclusive = false}
                    }
                }
            )
        }
    }
}