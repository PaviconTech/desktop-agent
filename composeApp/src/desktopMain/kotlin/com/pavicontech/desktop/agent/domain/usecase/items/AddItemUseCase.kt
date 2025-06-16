package com.pavicontech.desktop.agent.domain.usecase.items

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.request.AddItemReq
import com.pavicontech.desktop.agent.data.remote.dto.response.AddItemRes
import com.pavicontech.desktop.agent.domain.repository.ItemsRepository
import com.pavicontech.desktop.agent.presentation.helper.SnackbarController
import com.pavicontech.desktop.agent.presentation.helper.SnackbarEvent


class AddItemUseCase(
    private val repository: ItemsRepository,
    private val keyValueStorage: KeyValueStorage
) {
   suspend operator fun invoke(item: AddItemReq): AddItemRes? {
       return try {
           val token = keyValueStorage.get(Constants.AUTH_TOKEN)
               ?: return null
           val res = repository.addItem(token, item)
           SnackbarController.sendEvent(
               event = SnackbarEvent(
                   message = res.message,
               )
           )
           res
       } catch (e: Exception) {
           e.printStackTrace()
           null
       }
   }

}