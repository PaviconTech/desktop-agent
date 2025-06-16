package com.pavicontech.desktop.agent.domain.usecase.items

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.repository.ItemLocalRepository
import com.pavicontech.desktop.agent.domain.repository.ItemsRepository

class PullCodesUseCase(
    private val repository: ItemsRepository,
    private val itemLocalRepository: ItemLocalRepository,
    private val keyValueStorage: KeyValueStorage
) {
    suspend  operator fun invoke(){
        try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN) ?: return
            val res = repository.pullCodes(token)
            res?.let {
                itemLocalRepository.insertCodes(it)

            }
        }catch (e:Exception){
            println(e.message)
        }
    }
}