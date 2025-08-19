package com.pavicontech.desktop.agent.domain.usecase

import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetUserSessionStatus(
    private val keyValueStorage: KeyValueStorage
) {
    suspend operator fun invoke(): Boolean  = withContext(Dispatchers.IO) {
        keyValueStorage.get(Constants.AUTH_TOKEN)?.isNotBlank() ?: false
    }
}
