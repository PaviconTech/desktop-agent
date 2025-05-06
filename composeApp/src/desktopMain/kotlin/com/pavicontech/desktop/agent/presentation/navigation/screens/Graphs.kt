package com.pavicontech.desktop.agent.presentation.navigation.screens

import kotlinx.serialization.Serializable

sealed class Graphs() {
    @Serializable
    data object OnboardingGraph : Graphs()
    @Serializable
    data object DashboardGraph : Graphs()
}
