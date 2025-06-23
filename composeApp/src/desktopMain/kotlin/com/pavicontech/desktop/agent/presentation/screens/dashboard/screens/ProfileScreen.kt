package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.model.fromBusinessJson
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@Composable
fun ProfileScreen(
    onBack: () -> Unit,
) {
    val keyStorage: KeyValueStorage = koinInject()
    var profile: BusinessInformation? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var taxpayerName by remember { mutableStateOf("") }
    var kraPin by remember { mutableStateOf("") }
    var branchName by remember { mutableStateOf("") }
    var branchId by remember { mutableStateOf("") }
    var businessLogo by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        keyStorage.get(Constants.BUSINESS_INFORMATION)?.let {
            val loaded = it.fromBusinessJson()
            profile = loaded
            name = loaded.name
            taxpayerName = loaded.taxpayerName
            kraPin = loaded.kraPin
            branchName = loaded.branchName
            branchId = loaded.branchId
            businessLogo = loaded.businessLogo ?: ""
            address = loaded.address ?: ""
            email = loaded.email ?: ""
            phone = loaded.phone ?: ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 64.dp), // space for bottom button
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp,
            color = MaterialTheme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = "Edit Business Profile",
                        style = MaterialTheme.typography.h6.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.height(400.dp)) { // constrain scrollable area
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    EditableRow("Business Name", name) { name = it }
                                    EditableRow("KRA PIN", kraPin) { kraPin = it }
                                    EditableRow("Branch ID", branchId) { branchId = it }
                                    EditableRow("Address", address) { address = it }
                                    EditableRow("Email", email) { email = it }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    EditableRow("Taxpayer Name", taxpayerName) { taxpayerName = it }
                                    EditableRow("Branch Name", branchName) { branchName = it }
                                    EditableRow("Phone", phone) { phone = it }
                                    EditableRow("Business Logo", businessLogo) { businessLogo = it }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            profile?.let {
                                val updated = it.copy(
                                    name = name,
                                    taxpayerName = taxpayerName,
                                    kraPin = kraPin,
                                    branchName = branchName,
                                    branchId = branchId,
                                    locality = "",
                                    county = "",
                                    sectorName = "",
                                    sdcId = "",
                                    businessLogo = businessLogo,
                                    address = address,
                                    email = email,
                                    phone = phone
                                )
                                scope.launch {
                                    keyStorage.set(Constants.BUSINESS_INFORMATION, updated.toJsonString())
                                }
                            }
                        }
                    ) {
                        Text("Update Business Information")
                    }
                }
            }
        }
    }
}


@Composable
private fun EditableRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
            )
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.body1.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
