package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.presentation.screens.dashboard.DashboardViewModel
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.SalesScreenViewModel
import com.pavicontech.desktop.agent.presentation.screens.signIn.SignInScreenViewModel
import org.koin.dsl.module

val viewModelModules = module {
    single { SignInScreenViewModel(get(), get()) }
    factory { SalesScreenViewModel(get()) }        // fresh per screen
    single { DashboardViewModel(get()) }
}