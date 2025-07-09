package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings


import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFileUseCase
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFolderUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.LogsScreen
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.PDFCoordinateMapper
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.SettingScreenBody
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.SideColumnBody
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File

@Composable
fun SettingsScreen() {
    val keyValueStorage: KeyValueStorage = koinInject()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val openFile: SelectFolderUseCase = koinInject()
    var file: File? by remember { mutableStateOf(null) }
    val customPrintState = keyValueStorage.observe(Constants.PRINT_OUT_OPTIONS).collectAsState(initial = "")

    SettingScreenBody(
        sideColumn = {
            Column {
                SideColumnBody(
                    onSelectFile = {
                        scope.launch {
                            file = openFile.invoke()
                        }
                    },
                    onNavigateToLogs = {navController.navigate("logs"){navController.popBackStack()} },
                    onNavigateToPrintoutConfigurations = {navController.navigate("printout_configurations"){navController.popBackStack()} },
                )
            }
        },
        mainColumn = {

            NavHost(
                navController = navController,
                startDestination = "printout_configurations"
            ){

                composable(route = "printout_configurations") {
                    PDFCoordinateMapper(customPrintState.value == "custom_print")
                }

                composable(route = "logs") {
                    LogsScreen()
                }

            }

        }
    )
}
