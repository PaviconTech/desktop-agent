package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.domain.model.Sale
import com.pavicontech.desktop.agent.domain.usecase.sales.GetEtimsSalesUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components.SaleDetailsDialog
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components.SalesBody
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components.SalesUpperSection
import org.koin.compose.koinInject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SalesScreen() {
    val getEtimsSalesUseCase: GetEtimsSalesUseCase = koinInject()
    val sales = remember { mutableStateListOf<Sale>() }
    var refresh by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var isSaleLoading by remember { mutableStateOf(false) }


    LaunchedEffect(refresh) {
        isSaleLoading = true
        sales.clear()
        getEtimsSalesUseCase()?.let { sales.addAll(it) }
        isSaleLoading = false
    }

    val formatter = DateTimeFormatter.ISO_DATE_TIME

    val filteredSales = sales.filter {
        searchQuery.isBlank() ||
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                it.kraPin.contains(searchQuery, ignoreCase = true) ||
                it.invoiceNumber.contains(searchQuery, ignoreCase = true)
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
        SalesUpperSection(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
            }
        )
        SalesBody(
            sales = filteredSales,
            isLoading = isSaleLoading,
            onRefresh = { refresh = LocalDateTime.now().format(formatter) },
            onViewClick = {
            },
            onCreditNoteClick = {
                // Handle credit note click
            }
        )
    }
}

    
    
    
