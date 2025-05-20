package com.pavicontech.desktop.agent.presentation.navigation.screens

import kotlinx.serialization.Serializable



sealed class DashboardScreens {
    @Serializable
    data object Sales : DashboardScreens()
    @Serializable
    data object Items : DashboardScreens()
    @Serializable
    data object CreditNotes : DashboardScreens()
    @Serializable
    data object Customers : DashboardScreens()
    @Serializable
    data object Purchases : DashboardScreens()
    @Serializable
    data object Imports : DashboardScreens()
    @Serializable
    data object Insurance : DashboardScreens()
    @Serializable
    data object Reports : DashboardScreens()
    @Serializable
    data object SettingsScreen : DashboardScreens()
    @Serializable
    data class ProfileScreen(
        val id: Int = 0,
        val name: String = "",
        val branchId: String = "",
        val branchName: String = "",
        val districtName: String = "",
        val kraPin: String = "",
        val provinceName: String = "",
        val sectorName: String = "",
        val sdcId: String = "",
        val taxpayerName: String = ""
    ) : DashboardScreens()
}