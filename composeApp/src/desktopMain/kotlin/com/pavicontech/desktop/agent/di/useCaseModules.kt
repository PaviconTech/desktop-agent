package com.pavicontech.desktop.agent.di

import SubmitInvoicesUseCase
import com.pavicontech.desktop.agent.domain.usecase.FilePathWatcherUseCase
import com.pavicontech.desktop.agent.domain.usecase.GetSalesUseCase
import com.pavicontech.desktop.agent.domain.usecase.GetUserSessionStatus
import com.pavicontech.desktop.agent.domain.usecase.SignInUseCase
import org.koin.dsl.module


val useCaseModules = module {
    single { SignInUseCase(get(), get()) }
    single { FilePathWatcherUseCase(get()) }
    single{ GetUserSessionStatus(get()) }
    single{ SubmitInvoicesUseCase(get(), get(), get(), get()) }
    factory{ GetSalesUseCase(get()) }
}