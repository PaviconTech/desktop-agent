package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.usecase.receipt.GetAvailablePrintersUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import javax.print.PrintService

@Composable
fun SelectPrinter() {
    val getPrinters: GetAvailablePrintersUseCase = koinInject()
    val keyValueStorage: KeyValueStorage = koinInject()
    val scope = rememberCoroutineScope()

    val selectedPrinter by keyValueStorage.observe(Constants.SELECTED_PRINTER).collectAsState("")
    val printers = remember { mutableStateListOf<PrintService>() }
    var isExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val availablePrinters = getPrinters()
        availablePrinters.forEach { it.name.logger(Type.TRACE) }
        printers.addAll(availablePrinters)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "3. Select Printer",
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.body1
        )
        IconButton(onClick = { isExpanded = !isExpanded }) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand or collapse"
            )
        }
    }

    AnimatedVisibility(isExpanded) {
        LazyColumn {
            items(printers) { printer ->
                PrinterItem(
                    printerName = printer.name,
                    isSelected = selectedPrinter == printer.name,
                    onSelected = { name ->
                        scope.launch {
                            if (selectedPrinter?.isBlank() == true){
                                keyValueStorage.set(Constants.SELECTED_PRINTER, name)
                            } else keyValueStorage.set(Constants.SELECTED_PRINTER, "")
                        }
                    }
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PrinterItem(
    printerName: String,
    isSelected:Boolean,
    onSelected: (String) -> Unit
){
    Surface(
        onClick = { onSelected(printerName) },
        border = if (isSelected)
            BorderStroke(2.dp, MaterialTheme.colors.secondary)
        else
            BorderStroke(0.dp, MaterialTheme.colors.primary),
        color = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp),
        ) {
            Text(
                text = printerName,
                style = MaterialTheme.typography.body1
            )
        }
    }
}