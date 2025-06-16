package com.pavicontech.desktop.agent.presentation.screens.dashboard.items

data class AddItemState(
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val message: String = ""
)