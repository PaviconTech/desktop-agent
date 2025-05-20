package com.pavicontech.desktop.agent.domain.repository

import com.pavicontech.desktop.agent.data.remote.dto.response.getSales.GetSalesRes

interface SalesRepository {
    suspend fun getSales(token: String): GetSalesRes
}