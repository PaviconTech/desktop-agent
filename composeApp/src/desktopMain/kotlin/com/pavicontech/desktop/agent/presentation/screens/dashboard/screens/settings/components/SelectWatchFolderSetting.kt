package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.utils.Type
import com.pavicontech.desktop.agent.common.utils.logger
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFolderUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SelectWatchFolderSetting() {
    var isWatchFolderExpanded by remember { mutableStateOf(false) }
    val keyValueStorage: KeyValueStorage = koinInject()
    val observeWatchFolder by keyValueStorage.observe(Constants.WATCH_FOLDER).collectAsState("")
    val selectFolder: SelectFolderUseCase = koinInject()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "2. Select Watch Folder",
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.subtitle1
                )
                IconButton(
                    onClick = { isWatchFolderExpanded = !isWatchFolderExpanded }
                ) {
                    Icon(
                        imageVector = if (isWatchFolderExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand or collapse",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            // Divider for visual separation
            if (isWatchFolderExpanded) {
                Divider(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Expanded content
            AnimatedVisibility(isWatchFolderExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Display selected folder or placeholder
                    val folderText = if (observeWatchFolder?.isEmpty() == true) 
                        "No folder selected" 
                    else 
                        observeWatchFolder ?: "No folder selected"

                    // Folder display card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 0.dp,
                        backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.04f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Folder",
                                tint = MaterialTheme.colors.primary.copy(alpha = 0.7f),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = folderText,
                                style = MaterialTheme.typography.body2,
                                color = if (observeWatchFolder?.isEmpty() == true) 
                                    MaterialTheme.colors.onSurface.copy(alpha = 0.6f) 
                                else 
                                    MaterialTheme.colors.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Button to select folder
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
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .onKeyEvent { keyEvent ->
                                if (keyEvent.key == Key.Tab) {
                                    focusManager.moveFocus(FocusDirection.Next)
                                    true
                                } else false
                            },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Select Folder",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Select Folder")
                    }
                }
            }
        }
    }
}
