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
import com.pavicontech.desktop.agent.presentation.navigation.screens.Graphs
import com.pavicontech.desktop.agent.presentation.navigation.screens.OnboardingScreens
import com.pavicontech.desktop.agent.presentation.screens.signIn.components.SignInScreen
import com.pavicontech.desktop.agent.presentation.screens.splashScreen.SplashScreen

fun NavGraphBuilder.onboardingGraph(navController: NavHostController) {
    navigation<Graphs.OnboardingGraph>(
        startDestination = OnboardingScreens.Splash,
    ) {

        composable<OnboardingScreens.Splash>(
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
            SplashScreen(
                onNavigateToDashBoard = {
                    navController.navigate(Graphs.DashboardGraph){
                        navController.popBackStack()
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(OnboardingScreens.SignIn){
                        navController.popBackStack()
                    }
                }
            )
        }

        composable<OnboardingScreens.SignIn>(
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
            SignInScreen(
                onNavigateToPasswordReset = {

                },
                onNavigateToDashboard = {
                    navController.navigate(Graphs.DashboardGraph)
                }
            )
        }
    }
}