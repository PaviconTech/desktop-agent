package com.pavicontech.desktop.agent.presentation.screens.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun NavBarProfile(
    profileName: String,
    onProfile: () -> Unit
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Profile(onProfile = onProfile, profileName = profileName)
        Text(

            text = profileName,
            fontSize = 14.sp,
            color = MaterialTheme.colors.onBackground,
        )

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Profile(
    profileName: String,
    onProfile: () -> Unit
){
    Surface(
        onClick = {
            onProfile()
        },
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colors.secondary
        ),
        color = MaterialTheme.colors.secondary.copy(0.6f),
        contentColor = Color.White,
        modifier = Modifier.size(64.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment =Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = profileName.substring(
                    startIndex = 0,
                    endIndex = 1
                ),
                style = MaterialTheme.typography.h1,
            )
        }
    }
}