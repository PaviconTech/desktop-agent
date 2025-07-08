package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.creditNote.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes.Credit
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.status.components.toLocalFormattedString


@Composable
fun CreditNoteTable(
    creditNotes: List<Credit>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onViewClick: (Credit) -> Unit,
) {

// -- Inside SalesTable --
    val headers = listOf(
        "No.", "Invoice No.", "Original Invoice No.", "Items", "Amount",
        "Tax", "Credit Note Date", "Action"
    )

    val weights = listOf(
        0.5f, 1f, 1f, 1f, 1f, 1f, 1f, 1.5f
    )


    Column(modifier = Modifier.fillMaxWidth()) {
        CreditNoteTableHeader(headers, weights, onRefresh = onRefresh)
        CreditNoteTableBody(
            creditNotes = creditNotes.toList(),
            weights = weights,
            isLoading = isLoading,
            onViewClick = onViewClick,
        )
    }
}

@Composable
fun CreditNoteTableHeader(
    items: List<String>,
    weights: List<Float>,
    onRefresh: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        color = MaterialTheme.colors.primary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items.forEachIndexed { index, item ->
                if (index == items.lastIndex) {
                    // Last column with refresh button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(weights[index])
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = item,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onPrimary
                        )
                        IconButton(onClick = onRefresh) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colors.onPrimary
                            )
                        }
                    }
                } else {
                    Text(
                        text = item,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier
                            .weight(weights[index])
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun CreditNoteTableBody(
    isLoading: Boolean,
    creditNotes: List<Credit>,
    weights: List<Float>,
    onViewClick: (Credit) -> Unit,
) {
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        // LazyColumn with scrollbar
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 12.dp, start = 8.dp)
        ) {
            if (!isLoading) {
                itemsIndexed(creditNotes) { index, sale ->
                    CreditNoteTableItem(
                        creditNote = sale,
                        index = index,
                        weights = weights,
                        onViewClick = onViewClick
                    )
                }
            }
        }

        // Show Scrollbar always
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(listState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(end = 4.dp)
        )

        // Show loading spinner overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun CreditNoteTableItem(
    creditNote: Credit,
    index: Int,
    weights: List<Float>,
    onViewClick: (Credit) -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }


        CreditNoteDialog(
            sale = creditNote,
            isVisible = showDialog,
            onDismiss = { showDialog = false }
                )



    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = "${index + 1}",
            modifier = Modifier.weight(weights[0]),
            fontSize = 14.sp
        )
        Text(
            text = creditNote.invcNo.toString(),
            modifier = Modifier.weight(weights[1]),
            fontSize = 14.sp
        )
        Text(
            text = creditNote.orgInvcNo.toString(),
            modifier = Modifier.weight(weights[2]),
            fontSize = 14.sp
        )
        Text(
            text = "${creditNote.items.size}",
            modifier = Modifier.weight(weights[3]),
            fontSize = 14.sp
        )
        Text(
            text = creditNote.totAmt,
            modifier = Modifier.weight(weights[4]),
            fontSize = 14.sp
        )
        Text(
            text = creditNote.totTaxAmt,
            modifier = Modifier.weight(weights[5]),
            fontSize = 14.sp
        )
        Text(
            text = creditNote.createdAt.toLocalFormattedString(),
            modifier = Modifier.weight(weights[6]),
            fontSize = 14.sp
        )
        Row(
            modifier = Modifier.weight(weights[7]),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { showDialog = true },
                modifier = Modifier.weight(0.4f)
            ) {
                Text("View", fontSize = 12.sp)
            }
        }
    }
}









