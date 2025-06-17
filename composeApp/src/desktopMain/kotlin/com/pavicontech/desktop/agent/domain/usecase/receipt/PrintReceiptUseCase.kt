package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorageImpl
import com.pavicontech.desktop.agent.data.local.cache.createDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPageable
import java.awt.print.PrinterJob
import java.io.File
import javax.print.*
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Copies

class PrintReceiptUseCase(
    private val keyValueStorage: KeyValueStorage
) {
    suspend operator  fun invoke(filePath: String): Pair<String, Boolean> = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) return@withContext "PDF file not found." to false


        try {
            val printerName = keyValueStorage.get(Constants.SELECTED_PRINTER)?.trim()
            "Selected printer: '$printerName'".logger(Type.INFO)


            val document = PDDocument.load(file)

            val job = PrinterJob.getPrinterJob()

            // Find the printer
            val printServices = PrinterJob.lookupPrintServices()
            val selectedPrinter = printServices.find {
                it.name.trim().equals(printerName?.trim(), ignoreCase = true)
            }

            if (selectedPrinter == null) {
                "Printer '$printerName' not found.".logger(Type.WARN)
                return@withContext "Printer not found." to false
            }

            job.printService = selectedPrinter
            job.setPageable( PDFPageable(document))

            "Sending PDF to printer: ${selectedPrinter.name}".logger(Type.INFO)
            job.print()
            document.close()

            return@withContext "PDF printed successfully." to true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Failed to print PDF: ${e.message}" to false
        }
    }}




suspend fun main(){
    val printer = PrintReceiptUseCase(KeyValueStorageImpl(createDataStore()))
    println(printer.invoke("C:/Users/pasaka/Downloads/GA_life_Invoice%20(2).pdf"))
}

