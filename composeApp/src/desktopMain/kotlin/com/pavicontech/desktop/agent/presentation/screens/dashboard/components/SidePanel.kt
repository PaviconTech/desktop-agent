package com.pavicontech.printer.presentation.screens.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SidePanel(
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colors.primarySurface.copy(0.3f),
        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        SidePanelItem(name = "Home", icon1 = Icons.Default.Home, onClick = onNavigateToHome)
        SidePanelItem(name = "Settings", icon1 = Icons.Default.Settings, onClick = onNavigateToSettings)
    }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SidePanelItem(
    name:String,
    icon1:ImageVector? = null,
    icon2:Painter? = null,
    onClick : () -> Unit
){
    Surface(
        shape = CircleShape,
        color = Color.Transparent,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
            .padding(bottom = 8.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)

        ){
           if (icon1 != null && icon2 != null) {
               Icon(imageVector = icon1, contentDescription = name)
           }
            when{
                icon1 == null -> icon2?.let { Icon(painter = it, contentDescription = name) }
                icon2 == null ->  Icon(imageVector = icon1, contentDescription = name)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = name)
        }
    }
}