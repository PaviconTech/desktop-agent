package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.presentation.screens.dashboard.DashboardViewModel
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.home.SalesScreenViewModel
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.SettingsScreenViewModel
import com.pavicontech.desktop.agent.presentation.screens.signIn.SignInScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModules = module {
    single { SignInScreenViewModel(get(), get()) }
    factory { SalesScreenViewModel(get()) }        // fresh per screen
    single { SettingsScreenViewModel() }
    single { DashboardViewModel(get()) }
}