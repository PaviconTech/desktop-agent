package com.pavicontech.desktop.agent.di

import SubmitInvoicesUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.FilePathWatcherUseCase
import com.pavicontech.desktop.agent.domain.usecase.GetSalesUseCase
import com.pavicontech.desktop.agent.domain.usecase.GetUserSessionStatus
import com.pavicontech.desktop.agent.domain.usecase.SignInUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFileUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFolderUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemsUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeAndKraInfoUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GetAvailablePrintersUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.InsertQrCodeToInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintReceiptUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.RenderAndSavePdfUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.CreateSaleUseCase
import org.koin.dsl.module


val useCaseModules = module {
    single { SignInUseCase(get(), get()) }
    single { FilePathWatcherUseCase(get()) }
    single{ GetUserSessionStatus(get()) }
    single{ SubmitInvoicesUseCase(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory{ GetSalesUseCase(get()) }

    single { GenerateHtmlReceipt(get()) }
    single { GenerateQrCodeUseCase() }
    single { RenderAndSavePdfUseCase() }
    single { PrintReceiptUseCase(get()) }

    single { SelectFolderUseCase(get()) }
    single { SelectFileUseCase(get()) }
    single { InsertQrCodeToInvoiceUseCase() }
    single { GenerateQrCodeAndKraInfoUseCase(get(), get()) }


    single { GetItemsUseCase(get(), get(), get()) }
    single { CreateSaleUseCase(get(), get()) }
    single { GetAvailablePrintersUseCase() }
}