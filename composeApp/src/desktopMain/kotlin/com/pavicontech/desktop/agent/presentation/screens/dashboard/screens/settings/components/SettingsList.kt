package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
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

@Composable
fun SettingsList(
    content: @Composable () -> Unit
) {
    var isPrintOutConfigExpanded by remember { mutableStateOf(false) }
    var isWatchFolderExpanded by remember { mutableStateOf(false) }

    var keyValueStorage: KeyValueStorage = koinInject()
    val observeWatchFolder by keyValueStorage.observe(Constants.WATCH_FOLDER).collectAsState("")
    val selectFolder:SelectFolderUseCase = koinInject()
    val scope = rememberCoroutineScope()
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
                onClick = { isPrintOutConfigExpanded = !isPrintOutConfigExpanded }) {
                Icon(
                    imageVector = if (isPrintOutConfigExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand or collapse"
                )
            }
        }
        AnimatedVisibility(isPrintOutConfigExpanded) {
            content()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "2. Select Watch Folder",
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body1
            )
            IconButton(
                onClick = { isWatchFolderExpanded = !isWatchFolderExpanded }) {
                Icon(
                    imageVector = if (isWatchFolderExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand or collapse"
                )
            }
        }
        AnimatedVisibility(isWatchFolderExpanded) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                (if (observeWatchFolder?.isEmpty() == true) "Select Watch Folder" else observeWatchFolder)?.let {
                    Text(
                        text = it,
                        modifier = Modifier.width(150.dp)

                    )
                }

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            selectFolder.invoke()?.let {
                                keyValueStorage.set(
                                    Constants.WATCH_FOLDER,
                                    it.path
                                )
                                it.path.logger(Type.TRACE)
                            }

                        }
                    }
                ){
                    Text("Select Folder")
                }
            }
        }
    }
}