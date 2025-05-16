package com.pavicontech.desktop.agent.presentation.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.presentation.navigation.screens.DashboardScreens
import desktopagent.composeapp.generated.resources.Res
import desktopagent.composeapp.generated.resources.logout
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SidePanel(
    onNavigateToHome: () -> Unit,
    onNavigateToItems: () -> Unit,
    onNavigateToCreditNotes: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToImports: () -> Unit,
    onNavigateToInsurance: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogOut: () -> Unit,
    currentRoute: DashboardScreens?
) {
    Box(
        modifier = Modifier.fillMaxSize().background(
            color = MaterialTheme.colors.primary,
        )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Etims",
                        style = MaterialTheme.typography.h1,
                        color = MaterialTheme.colors.onPrimary
                    )
                    Text(
                        text = "Sync",
                        style = MaterialTheme.typography.h1,
                        color = MaterialTheme.colors.secondary
                    )
                }
                SidePanelItem(isSelected = currentRoute == DashboardScreens.Sales, name = "Sales", icon1 = Icons.Default.ShoppingCart, onClick = onNavigateToHome)
                SidePanelItem(isSelected = currentRoute == DashboardScreens.Items, name = "Items", icon1 = Icons.Default.ShoppingCart, onClick = onNavigateToItems)
                SidePanelItem(isSelected = currentRoute == DashboardScreens.CreditNotes, name = "Credit Notes", icon1 = Icons.Default.ShoppingCart, onClick = onNavigateToCreditNotes)
                SidePanelItem(isSelected = currentRoute == DashboardScreens.Customers, name = "Customers", icon1 = Icons.Default.ShoppingCart, onClick = onNavigateToCustomers)
                SidePanelItem(isSelected = currentRoute == DashboardScreens.Purchases, name = "Purchases", icon1 = Icons.Default.ShoppingCart, onClick = onNavigateToPurchases)
                SidePanelItem(isSelected = currentRoute == DashboardScreens.Imports, name = "Imports", icon1 = Icons.Default.ShoppingCart, onClick = onNavigateToImports)
                SidePanelItem(isSelected = currentRoute == DashboardScreens.Reports, name = "Reports", icon1 = Icons.Default.ShoppingCart, onClick = onNavigateToReports)
                SidePanelItem(isSelected = currentRoute == DashboardScreens.SettingsScreen, name = "Settings", icon1 = Icons.Default.Settings, onClick = onNavigateToSettings)
            }

            Surface(
                color = Color.Transparent,
                contentColor = Color.White,
                onClick = {
                    onLogOut()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Logout",
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(Res.drawable.logout),
                            contentDescription = "logout",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SidePanelItem(
    isSelected: Boolean,
    name: String, icon1: ImageVector? = null, icon2: Painter? = null, onClick: () -> Unit
) {
    Surface(
        contentColor = if (isSelected) Color.White else  MaterialTheme.colors.onPrimary,
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) MaterialTheme.colors.secondary else Color.Transparent,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(8.dp)

        ) {
            Text(
                text = name,
                style = if (isSelected) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (icon1 != null && icon2 != null) {
                Icon(imageVector = icon1, contentDescription = name)
            }
            when {
                icon1 == null -> icon2?.let {
                    Icon(
                        painter = it,
                        contentDescription = name,
                        modifier = Modifier.size(24.dp)
                    )
                }

                icon2 == null -> Icon(
                    imageVector = icon1,
                    contentDescription = name,
                    modifier = Modifier.size(24.dp)
                )
            }

        }
    }
}