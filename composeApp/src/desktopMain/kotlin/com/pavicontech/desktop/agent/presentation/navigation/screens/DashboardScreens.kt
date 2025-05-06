package com.pavicontech.desktop.agent.presentation.navigation.screens

import kotlinx.serialization.Serializable



sealed class DashboardScreens {
    @Serializable
    data object HomeScreen : DashboardScreens()
    @Serializable
    data object SettingsScreen : DashboardScreens()
}