package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.presentation.screens.dashboard.DashboardViewModel
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.home.HomeScreenViewModel
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.SettingsScreenViewModel
import com.pavicontech.desktop.agent.presentation.screens.signIn.SignInScreenViewModel
import org.koin.dsl.module

val viewModelModules = module {
    single { SignInScreenViewModel(get(), get()) }
    single { HomeScreenViewModel(get()) }
    single { SettingsScreenViewModel() }
    single { DashboardViewModel(get()) }
}