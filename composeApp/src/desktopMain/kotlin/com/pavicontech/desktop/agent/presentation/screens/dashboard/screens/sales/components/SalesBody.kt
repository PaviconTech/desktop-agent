package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.domain.model.Sale




@Composable
fun SalesBody(
    sales: List<Sale>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onViewClick: (Sale) -> Unit,
    saleDto: SnapshotStateList<com.pavicontech.desktop.agent.data.remote.dto.response.getSales.Sale>
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)

    ) {
        SalesTable(
            saleDto = saleDto,
            sales = sales,
            isLoading = isLoading,
            onRefresh = onRefresh,
            onViewClick = onViewClick,
        )
    }
}