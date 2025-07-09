package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
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
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFolderUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsList(
    content: @Composable () -> Unit,
    onNavigateToLogs:()-> Unit,
    onNavigateToPrintoutConfigurations:()-> Unit,
) {
    var isPrintOutConfigExpanded by remember { mutableStateOf(false) }

    Column {
        PrinterOutConfiguration(
            isPrintOutConfigExpanded = isPrintOutConfigExpanded,
            onExpandClick = {
                isPrintOutConfigExpanded = it
                onNavigateToPrintoutConfigurations()

            },
            content = {content()}
        )
        SelectWatchFolderSetting()
        SelectPrinter()
        Surface(
            onClick = onNavigateToLogs,
            modifier = Modifier.fillMaxWidth()
                .height(40.dp)
        ) {
           Text(
               text = "4. Logs",
               color = MaterialTheme.colors.primary,
               fontWeight = FontWeight.Bold,
               style = MaterialTheme.typography.body1,
               modifier = Modifier.fillMaxWidth()
                   .padding(horizontal = 16.dp, vertical = 8.dp),
           )
        }
    }
}