package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.presentation.screens.signIn.SignInScreenViewModel
import org.koin.dsl.module

val viewModelModules = module {
    single { SignInScreenViewModel(get(), get()) }
}