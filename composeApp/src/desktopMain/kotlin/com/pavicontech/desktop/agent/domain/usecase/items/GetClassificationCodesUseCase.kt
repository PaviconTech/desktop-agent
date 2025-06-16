package com.pavicontech.desktop.agent.domain.usecase.items

import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.local.database.entries.ClassificationCode
import com.pavicontech.desktop.agent.data.local.database.repository.ItemLocalRepository
import com.pavicontech.desktop.agent.domain.repository.ItemsRepository

class GetClassificationCodesUseCase(
    private val itemsLocalRepository: ItemLocalRepository

) {
    suspend operator fun invoke(): List<ClassificationCode>? = try {
        itemsLocalRepository.getAllClassificationCodes()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}