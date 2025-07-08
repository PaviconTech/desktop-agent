package com.pavicontech.desktop.agent.domain.usecase.sales

import androidx.compose.ui.graphics.Color
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes.GetAllCreditNotesRes
import com.pavicontech.desktop.agent.domain.repository.SalesRepository
import com.pavicontech.desktop.agent.presentation.helper.SnackbarController
import com.pavicontech.desktop.agent.presentation.helper.SnackbarEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class GetAllCreditNotesUseCase(
    private val repository: SalesRepository,
    private val keyValueStorage: KeyValueStorage
) {
      suspend operator  fun invoke():GetAllCreditNotesRes?{
        try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN)
                ?: return null
            val response = repository.getAllCreditNotes(token)
            if (response.status) {
                return response
            } else {
                SnackbarController.sendEvent(
                    event = SnackbarEvent(message = "Error: ${response.message}", color = Color.Red)
                )
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SnackbarController.sendEvent(
                event = SnackbarEvent(message = "Error: ${e.message ?: "An unexpected error occurred"}", color = Color.Red)
            )
            return null
        }
    }
}
