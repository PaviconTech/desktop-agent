package com.pavicontech.desktop.agent.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.ConfigCatClient
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.response.signIn.BussinessInfo
import org.koin.compose.koinInject

@Composable
fun MaintenanceDialog(
    onDismiss: () -> Unit
) {
    val keyValueStorage: KeyValueStorage = koinInject()
    val client = ConfigCatClient

    var businessInfo  by  remember { mutableStateOf<BussinessInfo?>(null) }
    var agentId by remember { mutableStateOf("")}
    var isMaintenaceAvailable  = client.maintenanceModeFlow(agentId).collectAsState(initial = false).value

    LaunchedEffect(Unit){
        keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.let {
            businessInfo = BussinessInfo.toBusinessInfo(it)
            agentId = "${businessInfo?.pin}_${businessInfo?.branchId}"
            isMaintenaceAvailable = client.isMaintenanceMode(agentId)
            println(isMaintenaceAvailable)
        }
    }

    DialogWindow(
        visible = isMaintenaceAvailable,
        onCloseRequest = onDismiss,
        title = "System Maintenance",
        state = rememberDialogState(width = 600.dp, height = 400.dp),
        resizable = false
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.background,
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
                    text = "🛠️ Scheduled Maintenance",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "The system is currently under scheduled maintenance. " +
                            "Please try again later. We apologize for any inconvenience caused.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        }
    }
}
