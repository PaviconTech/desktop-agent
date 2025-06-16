package com.pavicontech.desktop.agent.domain.usecase.items

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.repository.ItemLocalRepository
import com.pavicontech.desktop.agent.domain.repository.ItemsRepository

class PullClassificationCodesUseCase(
    private val repository: ItemsRepository,
    private val itemLocalRepository: ItemLocalRepository,
    private val keyValueStorage: KeyValueStorage
) {
    suspend operator fun invoke() {
        try {
            val token = keyValueStorage.get(Constants.AUTH_TOKEN) ?: return
            repository.pullClassificationCodes(token)?.let {
                itemLocalRepository.insertClassificationCode(it.toClassificationCodes())
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}