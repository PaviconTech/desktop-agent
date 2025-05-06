package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BussinessInformation(
    businessName:String,
    businessEmail:String,
    businessPhone:String,
    businessAddress:String,
    kraPin:String,
    onBusinessNameChange:(String)->Unit,
    onBusinessEmailChange:(String)->Unit,
    onBusinessPhoneChange:(String)->Unit,
    onBusinessAddressChange:(String)->Unit,
    onKraPinChange:(String)->Unit,
    onSave:()->Unit
) {
    Card(
    modifier = Modifier.fillMaxWidth(0.5f)
    .wrapContentHeight()
    .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = businessName,
                onValueChange = { onBusinessNameChange(it) },
                label = { Text("Bussiness Name") },
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = kraPin,
                onValueChange = { onKraPinChange(it) },
                label = { Text("KRA Pin") },
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = businessEmail,
                onValueChange = { onBusinessEmailChange(it) },
                label = { Text("Business Email") },
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = businessPhone,
                onValueChange = { onBusinessPhoneChange(it) },
                label = { Text("Business Phone") },
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = businessAddress,
                onValueChange = { onBusinessAddressChange(it) },
                label = { Text("Business Address") },
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                   onSave()
                }
            ) {
                Text(
                    text = "Save"
                )
            }


        }
    }
}