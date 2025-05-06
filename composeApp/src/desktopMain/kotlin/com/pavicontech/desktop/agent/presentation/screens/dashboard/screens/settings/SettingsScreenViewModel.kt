package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SettingsScreenViewModel : ViewModel() {
    var businessName by mutableStateOf("")
        private set
    var businessEmail by mutableStateOf("")
        private set
    var businessPhone by mutableStateOf("")
        private set
    var businessAddress by mutableStateOf("")
        private set
    var kraPin by mutableStateOf("")
        private set

    fun onBusinessNameChange(value: String) {
        businessName = value
    }

    fun onBusinessEmailChange(value: String) {
        businessEmail = value
    }

    fun onBusinessPhoneChange(value: String) {
        businessPhone = value
    }

    fun onBusinessAddressChange(value: String) {
        businessAddress = value
    }

    fun onKraPinChange(value: String) {
        kraPin = value
    }

    suspend fun onSaveBusinessInformation() {


    }

    suspend fun openFolder(){
    }

    private fun observerBusinessInformationChange(){
        viewModelScope.launch {
        }
    }


    init {
        observerBusinessInformationChange()
    }

}