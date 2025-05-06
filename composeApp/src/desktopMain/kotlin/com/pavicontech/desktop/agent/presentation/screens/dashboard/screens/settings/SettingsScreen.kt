package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BussinessInformation
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.SelectWatchFolder
import kotlinx.coroutines.launch

import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SettingsScreen() {
    val viewModel: SettingsScreenViewModel = koinViewModel()
    val scope = rememberCoroutineScope()

    Surface (
        color = MaterialTheme.colors.primary.copy(0.1f)
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Settings")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                BussinessInformation(
                    businessName = viewModel.businessName,
                    businessEmail = viewModel.businessEmail,
                    businessPhone = viewModel.businessPhone,
                    businessAddress = viewModel.businessAddress,
                    kraPin = viewModel.kraPin,
                    onBusinessNameChange = viewModel::onBusinessNameChange,
                    onBusinessEmailChange = viewModel::onBusinessEmailChange,
                    onBusinessPhoneChange = viewModel::onBusinessPhoneChange,
                    onBusinessAddressChange = viewModel::onBusinessAddressChange,
                    onKraPinChange = viewModel::onKraPinChange,
                    onSave = { scope.launch { viewModel.onSaveBusinessInformation() } }

                )
                SelectWatchFolder(
                    onSelectFolder = { scope.launch { viewModel.openFolder() } }
                )
            }

        }
    }
}