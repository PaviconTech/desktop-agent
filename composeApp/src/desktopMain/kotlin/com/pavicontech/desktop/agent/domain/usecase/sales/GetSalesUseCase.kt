package com.pavicontech.desktop.agent.domain.usecase.sales

import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.repository.SalesRepository

class GetSalesUseCase(
    private val repository: SalesRepository,
    private val keyValueStorage: KeyValueStorage
) {
   // operator fun invoke() = flow {}
}