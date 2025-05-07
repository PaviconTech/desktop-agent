package com.pavicontech.desktop.agent

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.couchbase.lite.MutableDocument
import com.pavicontech.desktop.agent.di.initKoin
import com.pavicontech.desktop.agent.domain.usecase.FilePathWatcherUseCase
import com.pavicontech.desktop.agent.domain.usecase.SubmitInvoicesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

fun main() = application {
    initKoin()
    val invoice: SubmitInvoicesUseCase = koinInject()

    CoroutineScope(Dispatchers.IO).launch {
        invoice.invoke()
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
        // Access window size here
        val width = windowState.size.width
        val height = windowState.size.height

        println("Window size: width = $width, height = $height")

        App(width = width, height = height)
    }
}

