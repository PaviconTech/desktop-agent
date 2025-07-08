package com.pavicontech.desktop.agent.domain.usecase.sales

import androidx.compose.ui.graphics.Color
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.common.utils.generateTimestamp
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.request.AdjustStockReq
import com.pavicontech.desktop.agent.data.remote.dto.request.createCreditNoteSale.CreditNoteReq
import com.pavicontech.desktop.agent.data.remote.dto.response.adjustStock.AdjustStockRes

import com.pavicontech.desktop.agent.data.remote.dto.response.createSaleRes.CreateSaleRes
import com.pavicontech.desktop.agent.data.remote.dto.response.creditNoteRes.CreditNoteRes
import com.pavicontech.desktop.agent.domain.repository.SalesRepository
import com.pavicontech.desktop.agent.domain.usecase.items.AdjustmentType
import com.pavicontech.desktop.agent.presentation.helper.SnackbarController
import com.pavicontech.desktop.agent.presentation.helper.SnackbarEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateCreditNoteUseCase(
    private val repository: SalesRepository,
    private val keyValueStorage: KeyValueStorage
) {
    operator fun invoke(
        body: CreditNoteReq
    ): Flow<Resource<CreditNoteRes>> = flow {
        emit(Resource.Loading())
        try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN)
                ?:  return@flow emit(Resource.Error(message = "Session ended kindly logout and log in again"))
            val response = repository.createCreditNote(
                body = body,
                token = token
            )
            SnackbarController.sendEvent(
                event = SnackbarEvent(message = response.message)
            )
            if (!response.status) return@flow emit(Resource.Error(message = response.message))
            return@flow emit(Resource.Success(message = response.message, data = response))
        }catch (e: Exception){
            SnackbarController.sendEvent(
                event = SnackbarEvent(message = "An unexpected error occurred", color = Color.Red)
            )
            emit(Resource.Error(message = e.message ?: "An unexpected error occurred"))
            e.printStackTrace()
        }
    }
}