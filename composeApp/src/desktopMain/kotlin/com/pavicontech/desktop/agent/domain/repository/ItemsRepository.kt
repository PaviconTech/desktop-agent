package com.pavicontech.desktop.agent.domain.repository

import com.pavicontech.desktop.agent.data.remote.dto.request.AddItemReq
import com.pavicontech.desktop.agent.data.remote.dto.request.AdjustStockReq
import com.pavicontech.desktop.agent.data.remote.dto.response.AddItemRes
import com.pavicontech.desktop.agent.data.remote.dto.response.adjustStock.AdjustStockRes
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.GetItemsRes
import com.pavicontech.desktop.agent.data.remote.dto.response.pullClassificationCodes.PullClassificationCodes
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.PullCodesRes

interface ItemsRepository {
    suspend fun getItems(token: String): GetItemsRes
    suspend fun pullCodes(token: String): PullCodesRes?
    suspend fun pullClassificationCodes(token: String): PullClassificationCodes?
    suspend fun addItem(token: String, item: AddItemReq): AddItemRes
    suspend fun adjustStock(body: AdjustStockReq, token: String): AdjustStockRes
}