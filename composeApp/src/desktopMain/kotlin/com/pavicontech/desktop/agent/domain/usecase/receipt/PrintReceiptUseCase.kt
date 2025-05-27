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
            val selectedPrinterName = keyValueStorage.get(Constants.SELECTED_PRINTER)
            val file = File(filePath)
            if (!file.exists()) {
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
                return@withContext Pair("Printing completed successfully.", true)
            } catch (e: PrintException) {
                e.printStackTrace()
                return@withContext Pair("Printing failed: ${e.message}", false)
            }
        }

}

