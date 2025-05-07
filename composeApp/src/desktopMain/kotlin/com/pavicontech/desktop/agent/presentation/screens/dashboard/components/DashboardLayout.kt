package com.pavicontech.desktop.agent.presentation.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardLayout(
    sidePanel: @Composable () -> Unit,
    navBar: @Composable () -> Unit,
    mainPanel: @Composable () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(200.dp) // Sidebar width
        ) {
            sidePanel()
        }

        // Right section: Top navbar + main content
        Column(modifier = Modifier.fillMaxSize()) {
            // Top navbar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Navbar height
            ) {
                navBar()
            }

            // Main panel content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp) // Optional padding
            ) {
                mainPanel()
            }
        }
    }
}
