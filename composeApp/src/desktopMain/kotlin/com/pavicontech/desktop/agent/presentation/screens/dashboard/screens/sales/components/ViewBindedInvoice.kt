package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.pavicontech.desktop.agent.domain.usecase.receipt.PrintReceiptUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.loadPdfFirstPageAsImage
import kotlinx.coroutines.launch
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.paragraph.Alignment
import org.koin.compose.koinInject
import java.awt.Desktop
import java.io.File

@Composable
fun ViewBindedInvoice(
    file: File?,
    show: Boolean,
    onDismiss: () -> Unit
) {
    val printReceiptUseCase: PrintReceiptUseCase = koinInject()
    val scope = rememberCoroutineScope()
    var invoice by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(file){
        file?.let {
            invoice = loadPdfFirstPageAsImage(file)
        }
    }
    DialogWindow(
        visible = show,
        onCloseRequest = onDismiss,
        title = file?.name ?: "Binded Invoice",
        state = rememberDialogState(width = 800.dp, height = 700.dp),
        resizable = true
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            invoice?.let {
                Image(
                    bitmap = it, contentDescription = "PDF Page",
                )
            }
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 16.dp)
                    .align(androidx.compose.ui.Alignment.TopEnd)
            ) {
                OutlinedButton(
                    onClick = {
                        if (file?.exists() == false) {
                            println("File does not exist: ${file.absolutePath}")
                        }

                        try {
                            val uri = file?.toURI()
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().browse(uri)
                            } else {
                                println("Desktop not supported on this platform")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                ){
                    Text(text = "Open In Browser")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        file?.let {
                            scope.launch {
                                printReceiptUseCase.invoke(it.absolutePath)
                            }
                        }
                    }
                ){
                    Text(text = "Print")
                }
            }
        }

    }
}