package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales

import com.pavicontech.desktop.agent.domain.model.Invoice

data class GetSalesState(
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val invoices: List<Invoice> = emptyList(),
    val error: String = "",

)
