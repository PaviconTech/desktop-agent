package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pavicontech.desktop.agent.domain.model.Sale
import com.pavicontech.desktop.agent.domain.usecase.sales.GenerateQRBitmap
import org.koin.compose.koinInject

@Composable
fun SaleDetailsDialog(
    sale: Sale,
    onDismiss: () -> Unit
) {
    val generateQRBitmap:GenerateQRBitmap = koinInject()
    val pin = "P051901215P"
    val bhfId = "00"
    val qrUrl ="https://etims-sbx.kra.go.ke/common/link/etims/receipt/indexEtimsReceiptData?Data=$pin$bhfId${sale.receiptSign}"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colors.background,
            modifier = Modifier
                .width(800.dp)
                .padding(16.dp),
            elevation = 16.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sale Details",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Sale Basic Info
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SaleInfoRow("Customer Name", sale.customerName)
                    SaleInfoRow("KRA PIN", sale.kraPin)
                    SaleInfoRow("Reference No.", sale.referenceNumber)
                    SaleInfoRow("Invoice No.", sale.invoiceNumber)
                    SaleInfoRow("ETIMS Receipt", sale.etimsReceiptNumber)
                    SaleInfoRow("Status", sale.status)
                    SaleInfoRow("Created At", sale.createdAt)
                    SaleInfoRow("Items Count", sale.itemsCount.toString())
                    SaleInfoRow("Amount", "KES %.2f".format(sale.amount))
                    SaleInfoRow("Tax", "KES %.2f".format(sale.tax))
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        bitmap = generateQRBitmap(qrUrl),
                        contentDescription = "Qr code",
                        modifier = Modifier
                            .weight(0.3f)
                            .size(200.dp)

                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f)
                    ) {
                        Text(
                            text = "Receipt Sign: ${sale.receiptSign}",
                            style = MaterialTheme.typography.body2,

                            )
                        Text(
                            text = "Intrl Data: ${sale.intrlData}",
                            style = MaterialTheme.typography.body2,

                        )

                    }
                }
            }
        }
    }
}


@Composable
fun SaleInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Text(text = value)
    }
}



