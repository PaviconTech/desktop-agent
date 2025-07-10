package com.pavicontech.desktop.agent.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.pavicontech.desktop.agent.common.BuildConfig
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.ConfigCatClient
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.response.signIn.BussinessInfo
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.awt.Desktop
import java.net.URI

@Composable
fun UpdateDialog(
    currentVersion: String = BuildConfig.VERSION
) {
    val keyValueStorage: KeyValueStorage = koinInject()
    val configClient = ConfigCatClient

    var agentId by remember { mutableStateOf("") }
    var businessInfo by remember { mutableStateOf<BussinessInfo?>(null) }

    var showDialog by remember { mutableStateOf(false) }
    var latestVersion = configClient.getLatestVersionFlow(agentId).collectAsState(initial = currentVersion).value
    var updateUrl by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit, latestVersion) {
        keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.let {
            businessInfo = BussinessInfo.toBusinessInfo(it)
            agentId = "${businessInfo?.pin}_${businessInfo?.branchId}"
            updateUrl = configClient.getUpdateUrl(agentId)
            latestVersion = configClient.getLatestVersion(agentId)
            showDialog = currentVersion != latestVersion        }
    }

    if (showDialog) {

        DialogWindow(

            onCloseRequest = {}, // Update is mandatory
            title = "Mandatory Software Update",
            state = rememberDialogState(width = 640.dp, height = 440.dp),
            resizable = false
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.surface,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📦 Mandatory Update Available!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "You're currently on version $currentVersion.\nPlease update to version $latestVersion to continue using this system.",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (updateUrl.isNotBlank()) {
                        Text(
                            text = "Click below to download the update:",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                try {
                                    Desktop.getDesktop().browse(URI(updateUrl))
                                } catch (e: Exception) {
                                    println("❌ Failed to open update URL: $e")
                                }
                            }
                        ) {
                            Text("Download & Install")
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "If the download doesn't start, copy this link:",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            SelectionContainer(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = updateUrl,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 8.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "⚠️ Update URL not available.",
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
