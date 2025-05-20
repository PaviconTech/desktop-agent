package com.pavicontech.desktop.agent.domain.usecase.receipt

import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
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

class PrintReceiptUseCase() {
    suspend operator fun invoke(filePath: String): Pair<String, Boolean?> = withContext(Dispatchers.IO) {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext (Pair("File not found: $filePath", false))

            }

            val printRequestAttributeSet = HashPrintRequestAttributeSet().apply {
                add(Copies(1))
            }

            val printService = PrintServiceLookup.lookupDefaultPrintService() ?: run {
                return@withContext Pair("No default printer found.", false)

            }


            val docPrintJob: DocPrintJob = printService.createPrintJob()

            val flavor = DocFlavor.INPUT_STREAM.PNG
            val doc: Doc = SimpleDoc(file.inputStream(), flavor, null)

            try {
                docPrintJob.print(doc, printRequestAttributeSet)
                "Printing started $filePath".logger(Type.INFO)
                return@withContext Pair("Printing completed successfully.", true)
            } catch (e: PrintException) {
                e.printStackTrace()
                return@withContext Pair("Printing failed: ${e.message}", false)
            }
        }

    }

