package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.status.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.DropdownMenuState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.usecase.AutoRetryUseCase
import com.pavicontech.desktop.agent.domain.usecase.invoices.GetFilteredInvoicesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.ObjectInputFilter.Status
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun StatusScreenBody() {
    val keyValueStorage: KeyValueStorage = koinInject()
    val getFilteredInvoices: GetFilteredInvoicesUseCase = koinInject()
    val autoRetry: AutoRetryUseCase = koinInject()
    val scope = rememberCoroutineScope()


    val selectedFilter by keyValueStorage.observe(Constants.INVOICE_STATUS_FILTER)
        .collectAsState("ALL")
    val filteredInvoices by getFilteredInvoices(selectedFilter ?: "ALL")
        .map { list -> list.sortedByDescending { Instant.parse(it.createdAt) } }.collectAsState(emptyList())


            Surface(
                color = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier

                    ) {

                        Text(
                            text = "Filter: ",
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                        ) {
                            SelectFilterItem(
                                name = "Extraction Pending",
                            )

                        }
                    }

                    TextButton(
                        onClick = {
                            scope.launch {
                                autoRetry.invoke()
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.onPrimary
                        )
                    ) {
                        Text(
                            text = "Retry all",
                            color  =  MaterialTheme.colors.onPrimary,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            if (filteredInvoices.isNotEmpty()) {
                LazyColumn {
                    itemsIndexed(filteredInvoices) { index, invoice ->
                        InvoiceRow(
                            index = index + 1,
                            invoice = invoice,
                            onRefresh = {
                                scope.launch {
                                    /*selectedFilter?.let { it ->
                                        filteredInvoices =
                                            getFilteredInvoices(it).sortedByDescending {
                                                Instant.parse(it.createdAt)
                                            }
                                    }*/
                                }
                            }
                        )
                    }
                }
            }

        }


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SelectFilterItem(
        name: String,
    ) {
        val keyValueStorage: KeyValueStorage = koinInject()
        val selected by keyValueStorage.observe(Constants.INVOICE_STATUS_FILTER)
            .collectAsState("ALL")
        var state by remember { mutableStateOf(DropdownMenuState(DropdownMenuState.Status.Closed)) }
        val scope = rememberCoroutineScope()
        Box {
            Surface(
                color = Color.Transparent,
                onClick = {
                    state = DropdownMenuState(DropdownMenuState.Status.Open(Offset.Zero))
                },
                modifier = Modifier
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    Text(
                        text = when (selected) {
                            "ALL" -> "All"
                            "EXTRACTION_PENDING" -> "Extraction Pending"
                            "EXTRACTION_FAILED" -> "Extraction Failed"
                            "EXTRACTION_SUCCESSFUL" -> "Extraction Successful"
                            "ETIMS_PENDING" -> "eTims Pending"
                            "ETIMS_FAILED" -> "eTims Failed"
                            "ETIMS_SUCCESSFUL" -> "eTims Successful"
                            else -> "All"
                        },
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

            }
            DropdownMenu(
                state = state,
                onDismissRequest = {
                    state = DropdownMenuState(DropdownMenuState.Status.Closed)
                },
                modifier = Modifier
            ) {
                DropdownMenuItem(
                    onClick = {
                        scope.launch {
                            keyValueStorage.set(Constants.INVOICE_STATUS_FILTER, "ALL")
                            state = DropdownMenuState(DropdownMenuState.Status.Closed)
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = "All",
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        scope.launch {
                            keyValueStorage.set(
                                Constants.INVOICE_STATUS_FILTER,
                                "EXTRACTION_PENDING"
                            )
                            state = DropdownMenuState(DropdownMenuState.Status.Closed)
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = "Extraction Pending",
                    )
                }

                DropdownMenuItem(
                    onClick = {
                        scope.launch {
                            keyValueStorage.set(
                                Constants.INVOICE_STATUS_FILTER,
                                "EXTRACTION_FAILED"
                            )
                            state = DropdownMenuState(DropdownMenuState.Status.Closed)
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = "Extraction Failed",
                    )
                }

                DropdownMenuItem(
                    onClick = {
                        scope.launch {
                            keyValueStorage.set(
                                Constants.INVOICE_STATUS_FILTER,
                                "EXTRACTION_SUCCESSFUL"
                            )
                            state = DropdownMenuState(DropdownMenuState.Status.Closed)
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = "Extraction Successful",
                    )
                }

                DropdownMenuItem(
                    onClick = {
                        scope.launch {
                            keyValueStorage.set(Constants.INVOICE_STATUS_FILTER, "ETIMS_PENDING")
                            state = DropdownMenuState(DropdownMenuState.Status.Closed)
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = "eTims Pending",
                    )
                }

                DropdownMenuItem(
                    onClick = {
                        scope.launch {
                            keyValueStorage.set(Constants.INVOICE_STATUS_FILTER, "ETIMS_FAILED")
                            state = DropdownMenuState(DropdownMenuState.Status.Closed)
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = "eTims Failed",
                    )
                }


                DropdownMenuItem(
                    onClick = {
                        scope.launch {
                            keyValueStorage.set(Constants.INVOICE_STATUS_FILTER, "ETIMS_SUCCESSFUL")
                            state = DropdownMenuState(DropdownMenuState.Status.Closed)
                        }
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = "eTims Successful",
                    )
                }


            }
        }

    }

