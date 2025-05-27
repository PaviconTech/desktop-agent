package com.pavicontech.desktop.agent.domain.usecase.receipt

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.print.PrintService
import javax.print.PrintServiceLookup

class GetAvailablePrintersUseCase() {
    suspend operator fun invoke(): List<PrintService> = withContext(Dispatchers.IO) {
        return@withContext PrintServiceLookup.lookupPrintServices(null, null).toList()
    }
}