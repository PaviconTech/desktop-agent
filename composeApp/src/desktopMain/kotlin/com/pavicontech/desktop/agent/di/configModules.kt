package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.data.local.DATA_STORE_FILE_NAME
import com.pavicontech.desktop.agent.data.local.createDataStore
import com.pavicontech.desktop.agent.data.remote.KtorClient
import com.pavicontech.desktop.agent.domain.usecase.GetUserSessionStatus
import io.ktor.client.*
import org.koin.dsl.module

val configModules = module{
    single{
        createDataStore {
            DATA_STORE_FILE_NAME
        }
    }
    single<HttpClient> {
        KtorClient.create(get())
    }
}