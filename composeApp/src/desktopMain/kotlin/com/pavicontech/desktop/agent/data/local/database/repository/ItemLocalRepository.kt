package com.pavicontech.desktop.agent.data.local.database.repository

import com.pavicontech.desktop.agent.data.local.database.entries.ClassificationCode
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.PullCodesRes


interface ItemLocalRepository {
    suspend fun insertItem(item: Item)
    suspend fun insertClassificationCode(codes: List<ClassificationCode>)
    suspend fun getAllClassificationCodes(): List<ClassificationCode>
    suspend fun insertCodes(codes: PullCodesRes)
    suspend fun getAllCodes(): PullCodesRes
    suspend fun insertAllItemsItem(items:List<Item>)
    suspend fun getAllItems(): List<Item>
    suspend fun getItemById(id: Int): Item?
    suspend fun deleteItem(id: Int)
}
