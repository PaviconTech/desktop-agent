package com.pavicontech.desktop.agent.presentation.navigation.screens

import kotlinx.serialization.Serializable

sealed class AuthScreens {
    @Serializable
    data object SignIn : AuthScreens()
}