package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.data.local.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.KeyValueStorageImpl
import com.pavicontech.desktop.agent.data.repository.UserRepositoryImpl
import com.pavicontech.desktop.agent.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModules  = module {
    single<KeyValueStorage> { KeyValueStorageImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
}