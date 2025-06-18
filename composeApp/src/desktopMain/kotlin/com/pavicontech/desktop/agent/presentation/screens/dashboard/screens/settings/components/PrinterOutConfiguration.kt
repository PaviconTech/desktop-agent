package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun PrinterOutConfiguration(
    isPrintOutConfigExpanded: Boolean,
    onExpandClick: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    val keyValueStorage: KeyValueStorage = koinInject()
    val printer = keyValueStorage.observe(Constants.PRINTOUT_SIZE).collectAsState(initial = "")
    val invoiceNumberPrefix = keyValueStorage.observe(Constants.INVOICE_NO_PREFIX).collectAsState(initial = "")
    val scope = rememberCoroutineScope()
    var invoicePrefixText by remember(invoiceNumberPrefix.value) {
        mutableStateOf(invoiceNumberPrefix.value ?: "")
    }
    Column {
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
                onClick = { onExpandClick(!isPrintOutConfigExpanded) }) {
                Icon(
                    imageVector = if (isPrintOutConfigExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand or collapse"
                )
            }
        }
        AnimatedVisibility(isPrintOutConfigExpanded) {
            content()
        }
        AnimatedVisibility(isPrintOutConfigExpanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    RadioButton(
                        selected = printer.value == "80mm",
                        onClick = {
                            scope.launch {
                                keyValueStorage.set(key = Constants.PRINTOUT_SIZE, value = "80mm")
                            }
                        }
                    )
                    Text(
                        text = "80 MM Printer"
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    RadioButton(
                        selected = printer.value == "A4",
                        onClick = {
                            scope.launch {
                                keyValueStorage.set(key = Constants.PRINTOUT_SIZE, value = "A4")
                            }
                        }
                    )
                    Text(
                        text = "A4 Printer"
                    )
                }
            }

        }


        AnimatedVisibility(isPrintOutConfigExpanded) {
            OutlinedTextField(
                value = invoicePrefixText,
                placeholder = {
                    Text(text = "Enter Invoice No Prefix separated by comma")
                },
                label = {
                    Text(text = "Invoice No Prefix")
                },
                onValueChange = {
                    invoicePrefixText = it
                    // Optionally debounce this in a real app
                    scope.launch {
                        keyValueStorage.set(Constants.INVOICE_NO_PREFIX, it)
                    }                },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }

}