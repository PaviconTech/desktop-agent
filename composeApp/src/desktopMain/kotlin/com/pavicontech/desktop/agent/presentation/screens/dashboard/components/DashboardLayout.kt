package com.pavicontech.desktop.agent.presentation.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardLayout(
    sidePanel: @Composable () -> Unit,
    mainPanel: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        sidePanel()
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                mainPanel()
            }
        }
    }
}
