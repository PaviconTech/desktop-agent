package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.print.Doc
import javax.print.DocFlavor
import javax.print.DocPrintJob
import javax.print.PrintException
import javax.print.PrintServiceLookup
import javax.print.SimpleDoc
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Copies

class PrintReceiptUseCase(
    private val keyValueStorage: KeyValueStorage
) {
    suspend operator fun invoke(filePath: String): Pair<String, Boolean?> =
        withContext(Dispatchers.IO) {
            "Printing receipt...".logger(Type.INFO)
            val selectedPrinterName = keyValueStorage.get(Constants.SELECTED_PRINTER)
            "Selected Printer: $selectedPrinterName".logger(Type.INFO)
            val file = File(filePath)
            if (!file.exists()) {
                "Printing path does not exist: $filePath".logger(Type.WARN)
                return@withContext Pair("File not found: $filePath", false)
            }

            val printServices = PrintServiceLookup.lookupPrintServices(null, null)
            val printService = printServices.find { it.name == selectedPrinterName }
                ?: return@withContext Pair("Printer '$selectedPrinterName' not found.", false)

            val docPrintJob = printService.createPrintJob()
            val flavor = DocFlavor.INPUT_STREAM.PNG
            val doc: Doc = SimpleDoc(file.inputStream(), flavor, null)

            val printRequestAttributeSet = HashPrintRequestAttributeSet().apply {
                add(Copies(1))
            }

            try {
                docPrintJob.print(doc, printRequestAttributeSet)
                "Printing completed successfully.".logger(Type.INFO)
                return@withContext Pair("Printing completed successfully.", true)
            } catch (e: PrintException) {
                "Printing failed: ${e.message}".logger(Type.WARN)
                e.printStackTrace()
                return@withContext Pair("Printing failed: ${e.message}", false)
            }
        }

}

