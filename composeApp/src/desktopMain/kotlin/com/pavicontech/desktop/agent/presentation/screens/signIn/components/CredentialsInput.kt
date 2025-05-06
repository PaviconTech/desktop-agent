package com.pavicontech.desktop.agent.presentation.screens.signIn.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CredentialInput(
    isPasswordFilled:Boolean = false,
    value:String,
    onValueChange: (String) ->Unit,
    placeHolder:String = ""
){
    OutlinedTextField(
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeHolder) },
        label = { Text(placeHolder) },
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}