package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.status

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.status.components.StatusScreenBody

@Composable
fun StatusScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            StatusScreenBody()
        }
    }
}