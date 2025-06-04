package com.pavicontech.desktop.agent.presentation.screens.dashboard.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item
import com.pavicontech.desktop.agent.domain.model.Sale
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemsUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.GetEtimsSalesUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components.ItemsBody
import com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components.ItemsUpperSection
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components.SalesUpperSection
import org.koin.compose.koinInject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ItemsScreen() {
    val getItemsUseCase: GetItemsUseCase = koinInject()
    val items = remember { mutableStateListOf<Item>() }
    var refresh by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    var isSaleLoading by remember { mutableStateOf(false) }


    LaunchedEffect(refresh) {
        isSaleLoading = true
        items.clear()
        getItemsUseCase()?.let { items.addAll(it) }
        isSaleLoading = false
    }

    val formatter = DateTimeFormatter.ISO_DATE_TIME

    val filteredItems = items.filter {
        searchQuery.isBlank() ||
                it.itemName.contains(searchQuery, ignoreCase = true) ||
                it.itemCode.contains(searchQuery, ignoreCase = true) ||
                it.itemClassificationCode.contains(searchQuery, ignoreCase = true) ||
                it.barcode?.contains(searchQuery, ignoreCase = true) == true
    }.sortedByDescending {
        runCatching {
            LocalDateTime.parse(
                it.createdAt,
                formatter
            )
        }.getOrElse { LocalDateTime.MIN }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        ItemsUpperSection(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
            }
        )
        ItemsBody(
            items = filteredItems,
            isLoading = isSaleLoading,
            onRefresh = {
                refresh = LocalDateTime.now().format(formatter)
            }
        )

    }


}