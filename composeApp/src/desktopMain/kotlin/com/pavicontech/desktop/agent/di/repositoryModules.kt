package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.data.fileSystem.DirectoryWatcherRepository
import com.pavicontech.desktop.agent.data.fileSystem.DirectoryWatcherRepositoryIml
import com.pavicontech.desktop.agent.data.local.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.KeyValueStorageImpl
import com.pavicontech.desktop.agent.data.repository.InvoiceRepositoryImpl
import com.pavicontech.desktop.agent.data.repository.UserRepositoryImpl
import com.pavicontech.desktop.agent.domain.repository.InvoiceRepository
import com.pavicontech.desktop.agent.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModules  = module {
    single<KeyValueStorage> { KeyValueStorageImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<DirectoryWatcherRepository> { DirectoryWatcherRepositoryIml() }
    single<InvoiceRepository> { InvoiceRepositoryImpl(get()) }
}