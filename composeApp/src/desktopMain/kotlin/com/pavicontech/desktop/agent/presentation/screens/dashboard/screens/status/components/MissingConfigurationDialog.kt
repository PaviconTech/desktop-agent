package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.status.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import org.koin.compose.koinInject


import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay


@Composable
fun MissingConfigurationDialog(
    onNavigateToSettings: () -> Unit
) {
    val keyValueStorage: KeyValueStorage = koinInject()

    var invoiceWatchFolderPathConfiguration by remember { mutableStateOf<String?>(null) }
    var qrCodeCoordinatesConfiguration by remember { mutableStateOf<String?>(null) }
    var kraInformationConfiguration by remember { mutableStateOf<String?>(null) }
    var mappedInvoiceConfigurations by remember { mutableStateOf<String?>(null) }

    // Fetch values on first load
    LaunchedEffect(Unit) {
        invoiceWatchFolderPathConfiguration = keyValueStorage.get(Constants.WATCH_FOLDER)
        qrCodeCoordinatesConfiguration = keyValueStorage.get(Constants.QR_CODE_COORDINATES)
        kraInformationConfiguration = keyValueStorage.get(Constants.KRA_INFO_COORDINATES)
        mappedInvoiceConfigurations = keyValueStorage.get(Constants.LAST_SELECTED_PRINTOUT_INVOICE)
    }

    val hasMissingConfig = listOf(
        invoiceWatchFolderPathConfiguration,
        qrCodeCoordinatesConfiguration,
        kraInformationConfiguration,
        mappedInvoiceConfigurations
    ).any { it.isNullOrBlank() }

    var isDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(hasMissingConfig) {
        if (hasMissingConfig) {
            delay(1000)
            isDialogVisible = true
        }
    }

    if (isDialogVisible) {
        DialogWindow(
            onCloseRequest = { isDialogVisible = false },
            title = "Missing Configuration",
            state = rememberDialogState(width = 800.dp, height = 500.dp),
            resizable = false
        ) {
            Surface(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Some required configurations are missing or incomplete.",
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.primary,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()

                    val configItems = listOf(
                        "Invoice Watch Folder Path" to invoiceWatchFolderPathConfiguration,
                        "QR Code Coordinates" to qrCodeCoordinatesConfiguration,
                        "KRA Info Coordinates" to kraInformationConfiguration,
                        "Last Selected Printout Invoice" to mappedInvoiceConfigurations
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(0.3f)
                            .fillMaxWidth()
                    ) {
                        items(configItems) { (label, configValue) ->
                            ConfigurationRow(label = label, isAvailable = !configValue.isNullOrBlank())
                        }
                    }


                    Text(
                        text="💡 How to fix:",
                        style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.primary)
                    )

                    Text(
                        "1. Go to *Settings > Printout Configuration*\n" +
                                "2. Select **Custom Printout** radio option\n" +
                                "3. Draw first box for **QR Code coordinates**, then second for **KRA Info coordinates**\n" +
                                "4. Under *Select Folder*, ensure `InvoiceWatch` directory is selected from the Desktop Agent folder inside Documents",
                        style = MaterialTheme.typography.body2
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                isDialogVisible = false
                                onNavigateToSettings()
                            }
                        ) {
                            Text("Fix Now")
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun ConfigurationRow(label: String, isAvailable: Boolean) {
    val icon = if (isAvailable) Icons.Default.CheckCircle else Icons.Default.Close
    val color = if (isAvailable) Color(0xFF4CAF50) else Color(0xFFF44336)
    val status = if (isAvailable) "Available" else "Missing"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = status,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.body1)
    }
}


