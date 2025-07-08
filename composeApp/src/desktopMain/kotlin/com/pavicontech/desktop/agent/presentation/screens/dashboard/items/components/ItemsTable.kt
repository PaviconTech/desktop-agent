package com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.DropdownMenuState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item
import com.pavicontech.desktop.agent.domain.model.Sale
import com.pavicontech.desktop.agent.domain.usecase.items.AdjustStockUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.AdjustmentType
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemsUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components.SaleDetailsDialog
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
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
            onRefresh = onRefresh
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
    onRefresh: () -> Unit
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
                        onRefresh = onRefresh
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
    onRefresh: () -> Unit
) {
    val adjustStockUseCase: AdjustStockUseCase = koinInject()
    val getItems: GetItemsUseCase = koinInject()
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    val selectedSale = remember { mutableStateOf<Item?>(null) }
    var showActionDropDown by remember { mutableStateOf(false) }
    var showAdjustStockItem by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    AdjustStockDialog(
        isLoading = isLoading,
        isVisible = showAdjustStockItem,
        onClose = { showAdjustStockItem = false },
        onAdjust = { reason, quantity, comments ->
            scope.launch {
                adjustStockUseCase.invoke(
                    itemId = item.id,
                    adjustmentType = reason,
                    amount = quantity,
                    reason = comments
                ).collect { result ->
                    isLoading = when(result){
                        is Resource.Loading -> {
                            true
                        }

                        is Resource.Success -> {
                            showAdjustStockItem = false
                            onRefresh()
                            true
                        }

                        is Resource.Error -> {
                            showAdjustStockItem = false
                            onRefresh()
                            true
                        }
                    }

                }
                showAdjustStockItem = false
            }
        }
    )


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

            Box(
                modifier = Modifier.weight(1f)
            ) {
                TextButton(
                    onClick = {
                        showActionDropDown = !showActionDropDown
                    },
                ) {
                    Text("Action")
                }
                DropdownMenu(
                    expanded = showActionDropDown,
                    onDismissRequest = { showActionDropDown = false }
                ) {
                    /*DropdownMenuItem(
                        onClick = {},
                    ) {
                        Text(text = "Edit Item")

                    }*/
                    DropdownMenuItem(
                        onClick = {
                            showAdjustStockItem = !showAdjustStockItem
                        },
                    ) {
                        Text(text = "Adjust Stock")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AdjustStockDialog(
    isVisible: Boolean,
    isLoading: Boolean,
    onClose: () -> Unit,
    onAdjust: (AdjustmentType, Int, String) -> Unit
) {
    var selectedReason by remember { mutableStateOf(AdjustmentType.INCOMING) }
    var quantity by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    val reasonOptions = AdjustmentType.values()
    var expanded by remember { mutableStateOf(false) }

    if (isVisible) {
        Dialog(onDismissRequest = onClose) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f),
                shape = RoundedCornerShape(12.dp),
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Title & Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Stock Adjustment",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.primary
                        )
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    // Reason Dropdown
                    Text("Reason", style = MaterialTheme.typography.body1)
                    Spacer(Modifier.height(4.dp))

                    Box {
                        Surface(
                            onClick = { expanded = true },
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.primary),
                            modifier = Modifier.fillMaxWidth()
                                .height(48.dp)
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = selectedReason.name
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )

                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            reasonOptions.forEach { reason ->
                                DropdownMenuItem(
                                    content = { Text(reason.name) },
                                    onClick = {
                                        selectedReason = reason
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Quantity Input
                    Text("Quantity", style = MaterialTheme.typography.body1)
                    Spacer(Modifier.height(4.dp))

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) quantity = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    // Comments Input
                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Enter comments") },
                        placeholder = {
                            Text("optional")
                        }
                    )

                    Spacer(Modifier.height(24.dp))

                    // Adjust Button
                    AnimatedVisibility(!isLoading){
                        Button(
                            onClick = {
                                onAdjust(selectedReason, quantity.toIntOrNull() ?: 0, comments)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Adjust")
                        }
                    }
                    AnimatedVisibility(
                        isLoading
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator()

                        }
                    }
                }
            }
        }
    }
}
