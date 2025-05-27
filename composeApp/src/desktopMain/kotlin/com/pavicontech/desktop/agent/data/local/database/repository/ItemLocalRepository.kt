package com.pavicontech.desktop.agent.data.local.database.repository

import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item


interface ItemLocalRepository {
    suspend fun insertItem(item: Item)
    suspend fun insertAllItemsItem(items:List<Item>)
    suspend fun getAllItems(): List<Item>
    suspend fun getItemById(id: Int): Item?
    suspend fun deleteItem(id: Int)
}
