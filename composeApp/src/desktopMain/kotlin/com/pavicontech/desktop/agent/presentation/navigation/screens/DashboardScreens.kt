package com.pavicontech.desktop.agent.presentation.navigation.screens

import kotlinx.serialization.Serializable



sealed class DashboardScreens {
    @Serializable
    data object HomeScreen : DashboardScreens()
    @Serializable
    data object SettingsScreen : DashboardScreens()
    @Serializable
    data class ProfileScreen(
        val id: Int,
        val name: String,
        val branchId: String,
        val branchName: String,
        val districtName: String,
        val kraPin: String,
        val provinceName: String,
        val sectorName: String,
        val sdcId: String,
        val taxpayerName: String
    ) : DashboardScreens()
}