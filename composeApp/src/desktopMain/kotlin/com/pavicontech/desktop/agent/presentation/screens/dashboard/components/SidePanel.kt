package com.pavicontech.desktop.agent.presentation.screens.dashboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemsUseCase
import com.pavicontech.desktop.agent.presentation.navigation.screens.DashboardScreens
import desktopagent.composeapp.generated.resources.Res
import desktopagent.composeapp.generated.resources.logout
import desktopagent.composeapp.generated.resources.sic
import desktopagent.composeapp.generated.resources.taxpoint_black
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SidePanel(
    onNavigateToHome: () -> Unit,
    onNavigateToStatus: () -> Unit,
    onNavigateToItems: () -> Unit,
    onNavigateToCreditNotes: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToImports: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    currentRoute: DashboardScreens?,
    profileName: String,
    onViewProfile: () -> Unit,
    onLogOut: () -> Unit

) {
    val getItems:GetItemsUseCase = koinInject()
    val scope   = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxWidth().background(
            color = MaterialTheme.colors.background,
        )
    ) {

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                ) {
                    Image(
                        painter = painterResource(Res.drawable.taxpoint_black),
                        contentDescription = "Taxpoint Logo",
                        modifier = Modifier.size(100.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                ) {
                    Text("Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            onLogOut()
                        },
                        ){
                        Icon(
                            painter = painterResource(Res.drawable.logout),
                            contentDescription = "logout",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    SidePanelItem(
                        isSelected = currentRoute == DashboardScreens.Status,
                        name = "Status",
                        onClick = onNavigateToStatus
                    )
                    SidePanelItem(
                        isSelected = currentRoute == DashboardScreens.Sales,
                        name = "Sales",
                        onClick = onNavigateToHome
                    )
                    SidePanelItem(
                        isSelected = currentRoute == DashboardScreens.Items,
                        name = "Items",
                        onClick = onNavigateToItems
                    )
                  SidePanelItem(
                        isSelected = currentRoute == DashboardScreens.CreditNotes,
                        name = "Credit Notes",
                        onClick = onNavigateToCreditNotes
                    )
                    /*
                   SidePanelItem(
                       isSelected = currentRoute == DashboardScreens.Customers,
                       name = "Customers",
                       onClick = onNavigateToCustomers
                   )
                   SidePanelItem(
                       isSelected = currentRoute == DashboardScreens.Purchases,
                       name = "Purchases",
                       onClick = onNavigateToPurchases
                   )
                   SidePanelItem(
                       isSelected = currentRoute == DashboardScreens.Imports,
                       name = "Imports",
                       onClick = onNavigateToImports
                   )
                   SidePanelItem(
                       isSelected = currentRoute == DashboardScreens.Reports,
                       name = "Reports",
                       onClick = onNavigateToReports
                   )*/
                    SidePanelItem(
                        isSelected = currentRoute == DashboardScreens.SettingsScreen,
                        name = "Settings",
                        onClick = onNavigateToSettings
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        onClick = {
                            scope.launch{
                                getItems.invoke()

                            }
                        },
                    ){
                        Icon(
                            painter = painterResource(Res.drawable.sic),
                            contentDescription = "sync",
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(24.dp)

                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    NavBarProfile(
                        profileName = profileName,
                        onProfile = onViewProfile
                    )
                }
            }

            Divider(
                color = MaterialTheme.colors.primary.copy(0.1f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 8.dp)

            )
        }




    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SidePanelItem(
    isSelected: Boolean,
    name: String,
    onClick: () -> Unit
) {
    Surface(
        contentColor = if (isSelected) Color.White else MaterialTheme.colors.onPrimary,
        shape = MaterialTheme.shapes.small,
        color =  Color.Transparent,
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(8.dp)

        ) {
            Text(
                textDecoration =  if (isSelected) TextDecoration.Underline else TextDecoration.None,
                text = name,
                style = MaterialTheme.typography.body1,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colors.primary
            )
        }
    }
}