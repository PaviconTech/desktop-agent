package com.pavicontech.printer.presentation.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun DashboardLayout(
    sidePanel:@Composable () -> Unit,
    mainPanel:@Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.fillMaxHeight()
                .width(150.dp)
        ) {
            sidePanel()
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            mainPanel()
        }
    }
}