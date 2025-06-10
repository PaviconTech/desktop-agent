package com.pavicontech.desktop.agent.presentation.screens.installationScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.presentation.helper.installPdfCreator
import com.pavicontech.desktop.agent.presentation.helper.logChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject


@Composable
fun InstallationScreen(
    onNavigateToPdfcreatorInstallationGuide: () -> Unit,

    ) {
    val keyValueStorage: KeyValueStorage = koinInject()
    val watchFolder by keyValueStorage.observe(Constants.WATCH_FOLDER).collectAsState(null)

    val logs = remember { mutableStateListOf<String>() }
    var isInstalling by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Idle") }
    val clipboardManager = LocalClipboardManager.current

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Collect logs from logChannel and append
    LaunchedEffect(Unit) {
        for (msg in logChannel) {
            logs.add(msg)
        }
    }

    // Auto-scroll to bottom when logs update
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.lastIndex)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                "📦 Pdfcreator Installer",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                "Status: $status",
                color = when {
                    status.contains("Success", ignoreCase = true) -> Color(0xFF4CAF50)
                    status.contains("Fail", ignoreCase = true) -> Color(0xFFF44336)
                    status.contains("Installing", ignoreCase = true) -> MaterialTheme.colors.primaryVariant
                    else -> MaterialTheme.colors.onBackground
                },
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Surface(
                color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        "What’s being installed:",
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.primaryVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• PDFCreator: a virtual printer that allows the app to generate PDF invoices from any printable source.\n" +
                                "• Additional components required for automation and file-saving features to work reliably in the background.",
                        style = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Why this is important:",
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.primaryVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "The Desktop Agent relies on PDFCreator to automatically generate, save, and monitor invoice files. "
                                + "Without this tool, the app cannot intercept print jobs or store PDF invoices for syncing with external systems like KRA eTIMS.",
                        style = MaterialTheme.typography.body2
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Important:",
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.primaryVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "During installation, dialog boxes may appear. Please accept all prompts and allow required permissions "
                                + "to ensure PDFCreator is installed correctly with full automation support.",
                        style = MaterialTheme.typography.body2
                    )
                }

            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface, shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                // Wrap LazyColumn in Row with vertical scrollbar
                Row {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        state = listState
                    ) {
                        items(logs) { log ->
                            Text(
                                text = log,
                                style = MaterialTheme.typography.body2.copy(fontFamily = FontFamily.Monospace),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    // Vertical scrollbar
                    VerticalScrollbar(
                        modifier = Modifier.fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState = listState)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (isInstalling) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (watchFolder != null) {
                        isInstalling = true
                        status = "Installing..."
                        logs.clear()
                        scope.launch(Dispatchers.IO) {
                            try {
                                installPdfCreator(
                                ) {
                                    withContext(Dispatchers.Main) {
                                        status = "✅ Success"
                                        isInstalling = false
                                        onNavigateToPdfcreatorInstallationGuide()
                                        keyValueStorage.set(Constants.INSTALLATION_PROCESS_STATUS, "true")
                                    }
                                }
                            } catch (e: Exception) {
                                logChannel.send("❌ Exception: ${e.localizedMessage}")
                                withContext(Dispatchers.Main) {
                                    status = "❌ Failed"
                                    isInstalling = false
                                }
                            }
                        }
                    } else {
                        status = "❗ Watch folder not set"
                    }
                },
                enabled = !isInstalling,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                elevation = ButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Text(if (isInstalling) "Installing..." else "Install")
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(logs.joinToString("\n")))
                    },
                    enabled = logs.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    elevation = ButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Text("📋 Copy Logs")
                }

                Button(
                    onClick = {
                        logs.clear()
                    },
                    enabled = logs.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                    elevation = ButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Text("🧹 Clear Logs", color = Color.White)
                }
            }
        }
    }
}
