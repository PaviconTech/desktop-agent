package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.creditNote.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.data.remote.dto.response.getAllCreditNotes.Credit

@Composable
fun CreditNoteBody(
    creditNotes: List<Credit>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onViewClick: (Credit) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)

    ) {
        CreditNoteTable(
            creditNotes = creditNotes,
            isLoading = isLoading,
            onRefresh = onRefresh,
            onViewClick = onViewClick,
            )
    }
}