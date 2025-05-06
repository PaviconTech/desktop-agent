package com.pavicontech.desktop.agent.presentation.navigation.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pavicontech.desktop.agent.presentation.navigation.screens.Graphs


@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Graphs.OnboardingGraph,
    ) {
        onboardingGraph(navController)
        composable<Graphs.DashboardGraph> {

        }
    }
}