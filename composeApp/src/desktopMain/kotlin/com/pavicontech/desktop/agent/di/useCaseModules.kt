package com.pavicontech.desktop.agent.di

import SubmitInvoicesUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.FilePathWatcherUseCase
import com.pavicontech.desktop.agent.domain.usecase.GetSalesUseCase
import com.pavicontech.desktop.agent.domain.usecase.GetUserSessionStatus
import com.pavicontech.desktop.agent.domain.usecase.SignInUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFolderUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeAndKraInfoUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.InsertQrCodeToInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintReceiptUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.RenderAndSavePdfUseCase
import org.koin.dsl.module


val useCaseModules = module {
    single { SignInUseCase(get(), get()) }
    single { FilePathWatcherUseCase(get()) }
    single{ GetUserSessionStatus(get()) }
    single{ SubmitInvoicesUseCase(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory{ GetSalesUseCase(get()) }

    single { GenerateHtmlReceipt(get()) }
    single { GenerateQrCodeUseCase() }
    single { RenderAndSavePdfUseCase() }
    single { PrintReceiptUseCase() }

    single { SelectFolderUseCase(get()) }
    single { InsertQrCodeToInvoiceUseCase() }
    single { GenerateQrCodeAndKraInfoUseCase(get(), get()) }
}