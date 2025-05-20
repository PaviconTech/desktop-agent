package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SalesTable() {
    val headers = listOf(
        "No.",
        "Date",
        "Cust. Name",
        "KRA PIN",
        "REF No.",
        "Invoice No.",
        "eTIMS Receipt No.",
        "Status",
        "Items",
        "Amount",
        "Tax",
        "Action"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SalesTableHeader(headers)
        SalesTableBody()
    }
}

@Composable
fun SalesTableHeader(
    items: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colors.primary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
        ) {
            items.forEach { item ->
                Text(
                    text = item,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onPrimary
                )
            }

        }
    }
}

@Composable
fun SalesTableBody() {

}