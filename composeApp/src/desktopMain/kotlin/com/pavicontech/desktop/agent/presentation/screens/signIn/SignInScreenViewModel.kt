package com.pavicontech.desktop.agent.presentation.screens.signIn

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.common.Resource
import com.pavicontech.desktop.agent.data.local.KeyValueStorage
import com.pavicontech.desktop.agent.domain.model.SavedUserCredentials
import com.pavicontech.desktop.agent.domain.usecase.SignInUseCase
import com.pavicontech.desktop.agent.presentation.helper.SnackbarController
import com.pavicontech.desktop.agent.presentation.helper.SnackbarEvent
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SignInScreenViewModel(
    private val signInUseCase: SignInUseCase,
    private val keyValueStorage: KeyValueStorage
):ViewModel() {

    var kraPin by mutableStateOf("")
        private set
    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set


    fun onKraPinChange(value:String){kraPin = value}
    fun onUsernameChange(value:String){username = value}
    fun onPasswordChange(value:String){password = value}



    var signInState by mutableStateOf(SignInState())
        private set


    fun signIn(){
        viewModelScope.launch {
            signInUseCase(kraPin, username, password).collect{result->
                println("Message: ${result.message}  data: ${result.data}")
                signInState =when(result){
                    is Resource.Loading -> {
                        SignInState(isLoading = true)
                    }
                    is Resource.Success -> {
                        SnackbarController.sendEvent(
                            event = SnackbarEvent(
                                message = "Sign In Success"
                            )
                        )
                        SignInState(isSuccessful = true, message = result.message ?: "")
                    }
                    is Resource.Error -> {
                        SnackbarController.sendEvent(
                            event = SnackbarEvent(
                                message = result.message ?: "An unexpected error occurred"
                            )
                        )
                        SignInState(message = result.message ?: "An unexpected error occurred")
                    }
                }
            }
        }
    }


    private fun getSavedUserCredentials(){
        viewModelScope.launch {
            keyValueStorage.get(Constants.SAVED_USER_CREDENTIALS)?.let {
                val obj = Json.decodeFromString<SavedUserCredentials>(it)
                kraPin = obj.kraPin
                username = obj.username
            }
        }
    }


    init {
        getSavedUserCredentials()
    }
}