package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.data.local.cache.DATA_STORE_FILE_NAME
import com.pavicontech.desktop.agent.data.local.cache.createDataStore
import com.pavicontech.desktop.agent.data.local.database.DatabaseConfig
import com.pavicontech.desktop.agent.data.remote.KtorClient
import io.ktor.client.*
import org.koin.dsl.module

val configModules = module{
    single{
        createDataStore {
            DATA_STORE_FILE_NAME
        }
    }
    single<HttpClient> {
        KtorClient.create()
    }


    single { DatabaseConfig }
}