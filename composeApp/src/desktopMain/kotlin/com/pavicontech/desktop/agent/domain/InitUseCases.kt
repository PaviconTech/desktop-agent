package com.pavicontech.desktop.agent.domain

import SubmitInvoicesUseCase
import com.pavicontech.desktop.agent.domain.usecase.AutoRetryUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemsUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.PullClassificationCodesUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.PullCodesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

class InitUseCases(
    private val invoices: SubmitInvoicesUseCase,
    private val items: GetItemsUseCase,
    private val autoRetry: AutoRetryUseCase,
    private val pullCodes: PullCodesUseCase,
    private val pullClassificationCodesUseCase: PullClassificationCodesUseCase

) {
    suspend operator fun invoke(): Unit = withContext(Dispatchers.IO + SupervisorJob()) {
        launch{ invoices() }
        launch{ items() }
        launch{ autoRetry() }
        launch{ pullCodes() }
        launch{ pullClassificationCodesUseCase() }
    }
}