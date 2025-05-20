package com.pavicontech.desktop.agent.presentation.screens.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import desktopagent.composeapp.generated.resources.Res
import desktopagent.composeapp.generated.resources.help
import org.jetbrains.compose.resources.painterResource


@Composable
fun NavBarProfile(
    profileName: String,
    onProfile: () -> Unit
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = MaterialTheme.colors.primary.copy(0.1f),
            shape = CircleShape
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(48.dp)
                    .padding(8.dp)
            ) {
                Profile(onProfile = onProfile, profileName = profileName)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = profileName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colors.primary,
                )
            }
            IconButton(
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(Res.drawable.help),
                    contentDescription = "help",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

        }
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
        modifier = Modifier.size(32.dp)
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
                style = MaterialTheme.typography.h6,
            )
        }
    }
}