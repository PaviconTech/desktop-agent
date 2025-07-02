package com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components

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
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item
import com.pavicontech.desktop.agent.domain.model.Sale
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components.SaleDetailsDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun ItemsTable(
    sales: List<Item>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
) {

    val headers = listOf(
        "No.",
        "Item Name",
        "Classification Code",
        "Item Code",
        "Product Category",
        "Price",
        "Tax Type",
        "Stock Qty",
        "Stock Status",
        "Action"
    )

    val weights = listOf(
        0.5f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.7f, 0.8f
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        ItemTableHeader(headers, weights, onRefresh = onRefresh)
        ItemsTableBody(
            items = sales.toList(),
            weights = weights,
            isLoading = isLoading,
        )
    }
}

@Composable
fun ItemTableHeader(
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
fun ItemsTableBody(
    isLoading: Boolean,
    items: List<Item>,
    weights: List<Float>,
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
                itemsIndexed(items) { index, item ->
                    ItemTableItem(
                        item = item,
                        index = index,
                        weights = weights,
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
fun ItemTableItem(
    item: Item,
    index: Int,
    weights: List<Float>,
) {

    var showDialog by remember { mutableStateOf(false) }
    val selectedSale = remember { mutableStateOf<Item?>(null) }

    if (showDialog) {
        ItemDetailsDialog(
            item = item,
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
        Text(text = item.itemName, modifier = Modifier.weight(weights[2]))
        Text(text = item.itemClassificationCode, modifier = Modifier.weight(weights[3]))
        Text(text = item.itemCode, modifier = Modifier.weight(weights[4]))
        Text(text = item.itemCategory?.category ?: "", modifier = Modifier.weight(weights[5]))
        Text(text = item.price, modifier = Modifier.weight(weights[7]))
        Text(text = item.taxCode, modifier = Modifier.weight(weights[8]))
        Text(text = item.currentStock, modifier = Modifier.weight(weights[9]))
        Text(
            text = if (item.currentStock.toLong() < 1)
                "No Stock"
            else if (item.currentStock.toLong() < 5)
                "Low Stock"
            else "In Stock",
            modifier = Modifier.weight(weights[10])
        )
        Row(
            modifier = Modifier.weight(weights[10]),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { selectedSale.value = item; showDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("View")
            }

            TextButton(
                onClick = {

                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Action")
            }
        }
    }
}
