package com.pavicontech.desktop.agent.presentation.screens.signIn

data class SignInState(
    val isLoading:Boolean = false,
    val isSuccessful:Boolean = false,
    val message:String = ""
)
