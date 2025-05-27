package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrinterOutConfiguration(
    isPrintOutConfigExpanded: Boolean,
    onExpandClick: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "1. Print Out Configurations",
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.body1
        )
        IconButton(
            onClick = { onExpandClick(!isPrintOutConfigExpanded )}) {
            Icon(
                imageVector = if (isPrintOutConfigExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand or collapse"
            )
        }
    }
    AnimatedVisibility(isPrintOutConfigExpanded) {
        content()
    }
}