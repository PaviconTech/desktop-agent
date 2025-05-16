package com.pavicontech.desktop.agent.presentation.screens.dashboard

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
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