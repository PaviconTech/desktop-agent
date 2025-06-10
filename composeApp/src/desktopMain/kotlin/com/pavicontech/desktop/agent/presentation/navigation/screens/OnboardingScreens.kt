package com.pavicontech.desktop.agent.presentation.navigation.screens

import kotlinx.serialization.Serializable

sealed class OnboardingScreens {
    @Serializable
    data object Splash : OnboardingScreens()
    @Serializable data object InstallationScreen : OnboardingScreens()
    @Serializable data object PdfCreatorInstallationGuide : OnboardingScreens()
}