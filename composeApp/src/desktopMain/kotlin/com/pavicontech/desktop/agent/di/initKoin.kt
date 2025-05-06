package com.pavicontech.desktop.agent.di

import org.koin.core.context.GlobalContext.startKoin

fun initKoin() {
    startKoin {
        modules(
            configModules,
            repositoryModules,
            useCaseModules,
            viewModelModules
        )
    }
}