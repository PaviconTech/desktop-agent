package com.pavicontech.desktop.agent

import SubmitInvoicesUseCase
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.pavicontech.desktop.agent.di.initKoin
import com.pavicontech.desktop.agent.domain.usecase.AutoRetryUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemsUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GetAvailablePrintersUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

fun main() = application {
    initKoin()

    val invoices: SubmitInvoicesUseCase = koinInject()
    val items : GetItemsUseCase = koinInject()
    val autoRetry : AutoRetryUseCase = koinInject()

    CoroutineScope(Dispatchers.IO).launch{ invoices() }
    CoroutineScope(Dispatchers.IO).launch{ items() }
    CoroutineScope(Dispatchers.IO).launch{ autoRetry() }
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized,
        size = DpSize(width = 1200.dp, height = 800.dp)

    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "Desktop Agent",
        state = windowState
    ) {
        val width = windowState.size.width
        val height = windowState.size.height

        App(width = width, height = height)
    }
}

