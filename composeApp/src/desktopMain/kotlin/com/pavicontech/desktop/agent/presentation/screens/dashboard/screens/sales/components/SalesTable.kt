package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.data.remote.dto.response.getSales.toCrediNoteReq
import com.pavicontech.desktop.agent.domain.model.Sale
import com.pavicontech.desktop.agent.domain.usecase.sales.CreateCreditNoteUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.status.components.toLocalFormattedString
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun SalesTable(
    sales: List<Sale>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onViewClick: (Sale) -> Unit,
    saleDto: SnapshotStateList<com.pavicontech.desktop.agent.data.remote.dto.response.getSales.Sale>
) {

    val headers = listOf(
        "No.", "Date", "Cust. Name", "KRA PIN", "REF No.",
        "Invoice No.", "Status", "Items",
        "Amount", "Tax", "Action"
    )

    val weights = listOf(
        0.5f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.7f, 0.8f
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        SalesTableHeader(headers, weights, onRefresh = onRefresh)
        SalesTableBody(
            sales = sales.toList(),
            saleDto = saleDto,
            weights = weights,
            isLoading = isLoading,
            onViewClick = onViewClick,
        )
    }
}

@Composable
fun SalesTableHeader(
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
fun SalesTableBody(
    isLoading: Boolean,
    sales: List<Sale>,
    weights: List<Float>,
    onViewClick: (Sale) -> Unit,
    saleDto: SnapshotStateList<com.pavicontech.desktop.agent.data.remote.dto.response.getSales.Sale>,

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
                itemsIndexed(sales) { index, sale ->
                    SaleTableItem(
                        saleDto = saleDto,
                        sale = sale,
                        index = index,
                        weights = weights,
                        onViewClick = {}
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
fun SaleTableItem(
    sale: Sale,
    index: Int,
    weights: List<Float>,
    onViewClick: (Sale) -> Unit,
    saleDto: SnapshotStateList<com.pavicontech.desktop.agent.data.remote.dto.response.getSales.Sale>
) {
    var isCreditNoteLoading by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    val selectedSale = remember { mutableStateOf<Sale?>(null) }
    val creditNoteUseCase: CreateCreditNoteUseCase = koinInject()
    val scope = rememberCoroutineScope()


    if (showDialog) {
        SaleDetailsDialog(
            sale = sale,
            onDismiss = {
                showDialog = false
            }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = "${index + 1}", modifier = Modifier.weight(weights[0]))
        Text(
            text = sale.createdAt.toLocalFormattedString(),
            modifier = Modifier.weight(weights[1])
        )
        Text(text = sale.customerName, modifier = Modifier.weight(weights[2]))
        Text(text = sale.kraPin, modifier = Modifier.weight(weights[3]))
        Text(text = sale.referenceNumber, modifier = Modifier.weight(weights[4]))
        Text(text = sale.invoiceNumber, modifier = Modifier.weight(weights[5]))
        Text(text = sale.status, modifier = Modifier.weight(weights[6]))
        Text(text = "${sale.itemsCount}", modifier = Modifier.weight(weights[7]))
        Text(text = "KES %.2f".format(sale.amount), modifier = Modifier.weight(weights[8]))
        Text(text = "KES %.2f".format(sale.tax), modifier = Modifier.weight(weights[9]))

        Row(
            modifier = Modifier
                .width(280.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(isCreditNoteLoading){
                CircularProgressIndicator(color = MaterialTheme.colors.error, modifier = Modifier.padding(70.dp)
                    .width(24.dp))
            }
            AnimatedVisibility(!isCreditNoteLoading){
                OutlinedButton(
                    onClick = {
                        val saleBody = saleDto.first { it.id.toString() == sale.id }
                        scope.launch {
                            creditNoteUseCase(body = saleBody.toCrediNoteReq()).collect { result->
                                isCreditNoteLoading = when(result){
                                    is Resource.Loading -> true
                                    is Resource.Success -> false
                                    is Resource.Error -> false
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colors.error
                    ),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.error),
                    modifier = Modifier.width(150.dp),
                ) {
                    Text("Credit Note")
                }
            }
            TextButton(
                onClick = { selectedSale.value = sale; showDialog = true },
                modifier = Modifier.width(80.dp)
            ) {
                Text("View")
            }
        }
    }
}