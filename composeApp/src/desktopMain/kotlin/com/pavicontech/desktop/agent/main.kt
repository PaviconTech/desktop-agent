package com.pavicontech.desktop.agent

import SubmitInvoicesUseCase
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.pavicontech.desktop.agent.di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

fun main() = application {
    initKoin()

    val invoices: SubmitInvoicesUseCase = koinInject()
    CoroutineScope(Dispatchers.IO).launch{
        invoices()
    }

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

