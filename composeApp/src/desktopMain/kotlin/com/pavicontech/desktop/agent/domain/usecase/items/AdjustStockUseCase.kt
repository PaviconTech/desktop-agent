package com.pavicontech.desktop.agent.domain.usecase.items

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.request.AdjustStockReq
import com.pavicontech.desktop.agent.data.remote.dto.response.adjustStock.AdjustStockRes
import com.pavicontech.desktop.agent.domain.repository.ItemsRepository
import com.pavicontech.desktop.agent.presentation.helper.SnackbarController
import com.pavicontech.desktop.agent.presentation.helper.SnackbarEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AdjustStockUseCase(
    private val itemsRepository: ItemsRepository,
    private val keyValueStorage: KeyValueStorage

) {
    operator fun invoke(
        itemId:Int,
        adjustmentType: AdjustmentType,
        amount:Int,
        reason: String? = null
    ): Flow<Resource<AdjustStockRes>> = flow {
        emit(Resource.Loading())
        try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN)
                ?:  return@flow emit(Resource.Error(message = "Session ended kindly logout and log in again"))
            val response = itemsRepository.adjustStock(
                body = AdjustStockReq(
                    itemId = itemId,
                    qty = amount,
                    reasonId = adjustmentType.type,
                    remark = reason
                ),
                token = token
            )
            SnackbarController.sendEvent(
                event = SnackbarEvent(message = response.message)
            )
            if (!response.status) return@flow emit(Resource.Error(message = response.message))
            return@flow emit(Resource.Success(message = response.message, data = response))
        }catch (e: Exception){
            emit(Resource.Error(message = e.message ?: "An unexpected error occurred"))
            e.printStackTrace()
        }
    }
}


enum class AdjustmentType(val type: String) {
    INCOMING("2"),
    OUTGOING(type = "4")
}