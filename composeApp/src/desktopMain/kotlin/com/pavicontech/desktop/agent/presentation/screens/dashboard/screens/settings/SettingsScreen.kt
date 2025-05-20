package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFolderUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.PDFCoordinateMapper
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.SettingScreenBody
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.SideColumnBody
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File

@Composable
fun SettingsScreen() {
    val keyValueStorage: KeyValueStorage = koinInject()
    val scope = rememberCoroutineScope()
    val openFolder: SelectFolderUseCase = koinInject()
    var file: File? by remember { mutableStateOf(null) }
    val customPrintState = keyValueStorage.observe(Constants.PRINT_OUT_OPTIONS).collectAsState(initial = "")

    SettingScreenBody(
        sideColumn = {
            Column {
                SideColumnBody(
                    onSelectFile = {
                        scope.launch {
                            file = openFolder.invoke()
                        }
                    }
                )
            }
        },
        mainColumn = {

            PDFCoordinateMapper(customPrintState.value == "custom_print")

        }
    )
}