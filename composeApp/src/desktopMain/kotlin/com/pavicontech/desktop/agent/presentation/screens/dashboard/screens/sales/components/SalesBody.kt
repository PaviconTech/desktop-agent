package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.domain.model.Sale

@Composable
fun SalesBody(
    sales: List<Sale>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onViewClick: (Sale) -> Unit,
    onCreditNoteClick: (Sale) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)

    ) {
        SalesTable(
            sales = sales,
            isLoading = isLoading,
            onRefresh = onRefresh,
            onViewClick = onViewClick,
            onCreditNoteClick = onCreditNoteClick
            )
    }
}