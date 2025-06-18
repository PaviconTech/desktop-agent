package com.pavicontech.desktop.agent.di

import RetryInvoicingUseCase
import SaveHtmlAsPdfUseCase
import SubmitInvoicesUseCase
import com.pavicontech.desktop.agent.domain.InitUseCases
import com.pavicontech.desktop.agent.domain.usecase.AutoRetryUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.FilePathWatcherUseCase
import com.pavicontech.desktop.agent.domain.usecase.GetSalesUseCase
import com.pavicontech.desktop.agent.domain.usecase.GetUserSessionStatus
import com.pavicontech.desktop.agent.domain.usecase.SignInUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFileUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFolderUseCase
import com.pavicontech.desktop.agent.domain.usecase.invoices.GetFilteredInvoicesUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.AddItemUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetClassificationCodesUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemCodesListUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemsUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.PullClassificationCodesUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.PullCodesUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateHtmlReceipt80MMUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeAndKraInfoUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GetAvailablePrintersUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.InsertQrCodeToInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintReceiptUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.RenderAndSavePdfUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.CreateCreditNoteUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.CreateSaleUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.DeleteInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.ExtractInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.GenerateQRBitmap
import com.pavicontech.desktop.agent.domain.usecase.sales.GetEtimsSalesUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintOutOptionUseCase
import org.koin.dsl.module


val useCaseModules = module {
    single { SignInUseCase(get(), get()) }
    single { FilePathWatcherUseCase(get()) }
    single{ GetUserSessionStatus(get()) }
    single{ SubmitInvoicesUseCase(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory{ GetSalesUseCase(get()) }
    single{ GetEtimsSalesUseCase(get(), get()) }
    single{ GenerateQRBitmap() }
    factory{ CreateCreditNoteUseCase(get(), get()) }
    factory{ ExtractInvoiceUseCase(get(), get(), get(),get(), get()) }
    factory{ PrintOutOptionUseCase(get(), get(), get(), get(), get()) }
    factory{ RetryInvoicingUseCase(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory{ AutoRetryUseCase(get(), get(), get()) }

    single { GenerateHtmlReceipt(get()) }
    single { GenerateQrCodeUseCase() }
    single { RenderAndSavePdfUseCase() }
    single { PrintReceiptUseCase(get()) }

    single { SelectFolderUseCase(get()) }
    single { SelectFileUseCase(get()) }
    single { DeleteInvoiceUseCase(get()) }
    single { InsertQrCodeToInvoiceUseCase() }
    single { GenerateQrCodeAndKraInfoUseCase(get(), get()) }
    single { GetFilteredInvoicesUseCase(get()) }


    single { GetItemsUseCase(get(), get(), get()) }
    single { CreateSaleUseCase(get(), get()) }
    single { GetAvailablePrintersUseCase() }
    single { GetItemCodesListUseCase(get()) }
    single { PullCodesUseCase(get(), get(), get()) }
    single { PullClassificationCodesUseCase(get(), get(), get()) }
    single { GetClassificationCodesUseCase(get()) }
    single { AddItemUseCase(get(), get()) }
    single { InitUseCases(get(), get(), get(), get(), get()) }
    single { SaveHtmlAsPdfUseCase() }
    single { GenerateHtmlReceipt80MMUseCase(get()) }
}