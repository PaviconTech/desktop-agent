package com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item
import com.pavicontech.desktop.agent.domain.model.Sale
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components.SalesTable



@Composable
fun ItemsBody(
    items: List<Item>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)

    ) {
        ItemsTable(
            sales = items,
            isLoading = isLoading,
            onRefresh = onRefresh
        )
    }
}