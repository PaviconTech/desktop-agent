package com.pavicontech.desktop.agent.di

import com.pavicontech.desktop.agent.data.fileSystem.DirectoryWatcherRepository
import com.pavicontech.desktop.agent.data.fileSystem.DirectoryWatcherRepositoryIml
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorageImpl
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepository
import com.pavicontech.desktop.agent.data.local.database.repository.InvoiceRepositoryImpl
import com.pavicontech.desktop.agent.data.repository.PDFExtractorRepositoryImpl
import com.pavicontech.desktop.agent.data.repository.UserRepositoryImpl
import com.pavicontech.desktop.agent.domain.repository.PDFExtractorRepository
import com.pavicontech.desktop.agent.domain.repository.UserRepository
import org.koin.dsl.module

val repositoryModules  = module {
    single<KeyValueStorage> { KeyValueStorageImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<DirectoryWatcherRepository> { DirectoryWatcherRepositoryIml() }
    single<PDFExtractorRepository> { PDFExtractorRepositoryImpl(get()) }
    single<InvoiceRepository> { InvoiceRepositoryImpl(get()) }
}