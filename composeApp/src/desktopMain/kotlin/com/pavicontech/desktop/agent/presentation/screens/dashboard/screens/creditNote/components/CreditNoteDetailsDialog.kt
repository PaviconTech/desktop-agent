package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.creditNote.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes.Credit
import com.pavicontech.desktop.agent.domain.model.fromBusinessJson
import com.pavicontech.desktop.agent.domain.usecase.sales.GenerateQRBitmap
import org.koin.compose.koinInject

@Composable
fun CreditNoteDialog(
    sale: Credit,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val generateQRBitmap: GenerateQRBitmap = koinInject()
    val keyValueStorage: KeyValueStorage = koinInject()

    var pin by remember { mutableStateOf("") }
    var bhfid by remember { mutableStateOf("") }
    var qrUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.let {
            val loaded = it.fromBusinessJson()
            pin = loaded.kraPin
            bhfid = loaded.branchId
            qrUrl = "${Constants.ETIMS_QR_URL}$pin$bhfid${sale.rcptSign}"
        }
    }

    DialogWindow(
        onCloseRequest = onDismiss,
        visible = isVisible,
        title = "Credit Note",
        state = rememberDialogState(width = 1000.dp, height = 800.dp),
        resizable = true
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // Top: Title & QR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Credit Note Details", style = MaterialTheme.typography.h5)
                        Text("Generated for invoice: ${sale.invcNo}", style = MaterialTheme.typography.subtitle2)
                    }

                    Image(
                        bitmap = generateQRBitmap(qrUrl),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Sales & Customer Information
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {

                    // Left Column
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sales Invoice", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        InfoRow("Invoice No.", sale.invcNo.toString())
                        InfoRow("Original Invoice No.", sale.orgInvcNo.toString(), highlight = true)
                        InfoRow("Total Amount", "KES ${sale.totAmt}", highlight = true)
                    }

                    // Right Column
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Customer", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFFF5722))
                        InfoRow("Name", sale.custNm ?: "-")
                        InfoRow("KRA PIN", sale.custTin ?: "-")
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Credit Note Details
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Credit Note Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    InfoRow("Reason", sale.remark ?: "-")
                    InfoRow("Note / Remarks", sale.status ?: "-")
                }

                Spacer(Modifier.height(24.dp))

                // Items Table Header
                Text("Sale Items", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D47A1))
                        .padding(vertical = 8.dp),
                ) {
                    val headers = listOf("No.", "Item Name", "Description", "Code", "Qty", "Price", "Amount", "Disc", "Tax", "Total")
                    headers.forEach {
                        Text(
                            it,
                            modifier = Modifier.weight(1f),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Items Table Body
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp)
                ) {
                    itemsIndexed(sale.items) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                "${index + 1}",
                                item.itemNm,
                                item.itemNmDef,
                                item.itemCd ,
                                item.qty ,
                                item.prc ,
                                item.splyAmt ,
                                item.dcAmt,
                                item.taxAmt,
                                item.totAmt
                            ).forEach {
                                Text(it, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun InfoRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(180.dp)
        )
        Text(
            text = value,
            color = if (highlight) Color(0xFF00C853) else MaterialTheme.colors.onBackground
        )
    }
}

