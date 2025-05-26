package com.pavicontech.desktop.agent.domain.usecase.items

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item
import com.pavicontech.desktop.agent.domain.repository.ItemsRepository
import com.pavicontech.desktop.agent.presentation.helper.SnackbarController
import com.pavicontech.desktop.agent.presentation.helper.SnackbarEvent

class GetItemsUseCase(
    private val repository: ItemsRepository,
    private val keyValueStorage: KeyValueStorage
) {
    suspend operator fun invoke(): List<Item>? {
       return try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN) ?: ""
            val response = repository.getItems(token)
            if (response.status){
                keyValueStorage.set(Constants.ITEM_LIST, response.toItemListString())
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Items Synced Successfully"
                    )
                )
            }
            response.items
        }catch (e: Exception){
            e.printStackTrace()
           null
        }
    }
}