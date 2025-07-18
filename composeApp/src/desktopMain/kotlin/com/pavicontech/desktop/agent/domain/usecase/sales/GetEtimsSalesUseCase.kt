package com.pavicontech.desktop.agent.domain.usecase.sales

import androidx.compose.ui.graphics.Paint
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.model.Sale
import com.pavicontech.desktop.agent.domain.repository.SalesRepository
import com.pavicontech.desktop.agent.presentation.helper.SnackbarController
import com.pavicontech.desktop.agent.presentation.helper.SnackbarEvent


class GetEtimsSalesUseCase(
    private val repository: SalesRepository,
    private val keyValueStorage: KeyValueStorage
) {
    suspend  operator fun invoke(): Pair<List<com.pavicontech.desktop.agent.data.remote.dto.response.getSales.Sale>,List<Sale>?>?  {
        try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN)
                ?: return null
            val sales = repository.getSales(token)
            if (sales.status) {
                return Pair(sales.sales, sales.toSale())
            } else {
                SnackbarController.sendEvent(
                    event = SnackbarEvent(message = "Error: ${sales.message}")
                )
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
