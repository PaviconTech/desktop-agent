package com.pavicontech.desktop.agent.domain.repository

import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.GetItemsRes

interface ItemsRepository {
    suspend fun getItems(token: String): GetItemsRes
}