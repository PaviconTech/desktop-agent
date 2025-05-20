package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.domain.model.Invoice
import java.time.format.DateTimeFormatter

@Composable
fun InvoiceCard(number: Int, invoice: Invoice) {
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                text = "Invoice #$number",
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colors.primary
            )

            Spacer(Modifier.height(12.dp))

            // Branch Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Branch",
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${invoice.branchName} (${invoice.branchId})",
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface
                )
            }

            Spacer(Modifier.height(8.dp))

            // Items, Item Price, Subtotal, Tax, Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Items Qty",
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${invoice.itemsQty}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Item Price",
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${invoice.itemPrice}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${invoice.subTotal ?: "N/A"}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tax",
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "${invoice.taxAmount}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.secondary
                )
                Text(
                    text = "${invoice.totalAmount}",
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colors.secondary
                )
            }

            Spacer(Modifier.height(8.dp))

            // Created At
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Created: ${
                        invoice.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    }",
                    style = MaterialTheme.typography.caption.copy(color = Color.Gray)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Icon(Icons.Outlined.PlayArrow, contentDescription = "View")
                    Spacer(Modifier.width(4.dp))
                    Text("View", fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Credit Note")
                    Spacer(Modifier.width(4.dp))
                    Text("Credit Note", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

