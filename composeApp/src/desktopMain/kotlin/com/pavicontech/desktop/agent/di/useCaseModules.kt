package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.domain.usecase.SignInUseCase
import org.koin.dsl.module


val useCaseModules = module {
    single { SignInUseCase(get(), get()) }
}