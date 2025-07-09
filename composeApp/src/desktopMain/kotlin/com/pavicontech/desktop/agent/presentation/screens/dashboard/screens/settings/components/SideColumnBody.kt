package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SideColumnBody(
    onSelectFile: @Composable () -> Unit,
    onNavigateToLogs:()-> Unit,
    onNavigateToPrintoutConfigurations:()-> Unit,
) {
    Surface(
        elevation = 8.dp,

    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SettingsList(
                content = {       SelectPrintOptions() },
                onNavigateToLogs = onNavigateToLogs,
                onNavigateToPrintoutConfigurations = onNavigateToPrintoutConfigurations,
            )
        }
    }
}


@Composable
fun SelectPrintOptions() {
    var keyValueStorage: KeyValueStorage = koinInject()
    val selectedOption = keyValueStorage.observe(Constants.PRINT_OUT_OPTIONS).collectAsState(initial = "")
    val scope = rememberCoroutineScope()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            RadioButton(
                selected = selectedOption.value == "default",
                onClick = {
                    scope.launch {
                        keyValueStorage.set(key = Constants.PRINT_OUT_OPTIONS, value = "default")
                    }
                }
            )
            Text(
                text = "Default Print"
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            RadioButton(
                selected = selectedOption.value == "custom_print",
                onClick = {
                    scope.launch {
                        keyValueStorage.set(key = Constants.PRINT_OUT_OPTIONS, value = "custom_print")
                    }
                }
            )
            Text(
                text = "Custom Print out"
            )
        }


    }
}