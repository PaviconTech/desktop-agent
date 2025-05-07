package com.pavicontech.desktop.agent.presentation.screens.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.KeyValueStorage
import com.pavicontech.desktop.agent.di.viewModelModules
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BussinessInformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val keyValueStorage: KeyValueStorage
) : ViewModel() {
    var businessProfile: BusinessInformation? by mutableStateOf(null)


    private fun getBusinessProfile() {
        viewModelScope.launch {
            keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.let {
                businessProfile = BusinessInformation.fromJsonString(it)
            }
        }
    }


    fun logout(){
        viewModelScope.launch {
            keyValueStorage.clear()
        }
    }

    init {
        getBusinessProfile()
    }
}