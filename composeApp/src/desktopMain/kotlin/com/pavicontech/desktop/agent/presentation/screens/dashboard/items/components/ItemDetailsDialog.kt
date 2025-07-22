package com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.pavicontech.desktop.agent.data.remote.dto.response.getItems.Item


@Composable
fun ItemDetailsDialog(
    item: Item,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focus = LocalFocusManager.current

    // State for edit mode
    var isEditMode by remember { mutableStateOf(false) }

    // Use mutableStateOf for editable fields
    var itemName by remember { mutableStateOf(item.itemName) }
    var itemClassification by remember { mutableStateOf(item.itemClassificationCode) }
    var itemType by remember { mutableStateOf(item.itemClassificationCode) }
    var originCountry by remember { mutableStateOf(item.originCountry) }
    var packagingType by remember { mutableStateOf(item.packagingUnit) }
    var quantityType by remember { mutableStateOf(item.quantityUnit) }
    var taxCode by remember { mutableStateOf(item.taxCode) }
    var barCode by remember { mutableStateOf(item.barcode ?: "") }
    var productCategory by remember { mutableStateOf(item.itemCategory?.category ?: "") }
    var price by remember { mutableStateOf(item.price) }

    // Helper function for keyboard navigation
    fun Modifier.onEnterNext() = onKeyEvent { key ->
        if (key.key == Key.Enter) {
            focus.moveFocus(FocusDirection.Next); true
        } else false
    }

    DialogWindow(
        onCloseRequest = onDismiss,
        title = "Item Details",
        state = rememberDialogState(width = 1200.dp, height = 850.dp),
        resizable = true
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header with title and close button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Item Details",
                            style = MaterialTheme.typography.h5.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colors.primary
                        )
                        Text(
                            text = "View and edit item information",
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                        )
                    }

                }

                Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f))


                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Basic Information Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Basic Information",
                                style = MaterialTheme.typography.subtitle1.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colors.primary
                            )

                            Spacer(Modifier.height(4.dp))

                            // Row 1
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                EditableField(
                                    label = "Item Name", 
                                    value = itemName, 
                                    onValueChange = { itemName = it },
                                    isEditable = isEditMode,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                                EditableField(
                                    label = "Item Classification", 
                                    value = itemClassification, 
                                    onValueChange = { itemClassification = it },
                                    isEditable = isEditMode,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                            }

                            // Row 2
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                EditableField(
                                    label = "Item Type", 
                                    value = itemType, 
                                    onValueChange = { itemType = it },
                                    isEditable = isEditMode,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                                EditableField(
                                    label = "Country of Origin", 
                                    value = originCountry ?: "",
                                    onValueChange = { originCountry = it },
                                    isEditable = isEditMode,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                            }
                        }

                        // Units and Packaging Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Units and Packaging",
                                style = MaterialTheme.typography.subtitle1.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colors.primary
                            )

                            Spacer(Modifier.height(4.dp))

                            // Row 3
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                EditableField(
                                    label = "Packaging Type", 
                                    value = packagingType ?: "",
                                    onValueChange = { packagingType = it },
                                    isEditable = isEditMode,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                                EditableField(
                                    label = "Quantity Type", 
                                    value = quantityType ?: "",
                                    onValueChange = { quantityType = it },
                                    isEditable = isEditMode,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                            }
                        }

                        // Additional Details Section
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Additional Details",
                                style = MaterialTheme.typography.subtitle1.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colors.primary
                            )

                            Spacer(Modifier.height(4.dp))

                            // Row 4
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                EditableField(
                                    label = "Tax Code", 
                                    value = taxCode, 
                                    onValueChange = { taxCode = it },
                                    isEditable = isEditMode,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                                EditableField(
                                    label = "Bar Code", 
                                    value = barCode, 
                                    onValueChange = { barCode = it },
                                    isEditable = isEditMode,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                            }

                            // Row 5
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                EditableField(
                                    label = "Product Category", 
                                    value = productCategory, 
                                    onValueChange = { productCategory = it },
                                    isEditable = isEditMode,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                                EditableField(
                                    label = "Unit Price", 
                                    value = price, 
                                    onValueChange = { price = it },
                                    isEditable = isEditMode,
                                    keyboardType = KeyboardType.Decimal,
                                    modifier = Modifier.weight(1f).onEnterNext()
                                )
                            }
                        }
                    }


                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel button (only show in edit mode)
                    if (isEditMode) {
                        OutlinedButton(
                            onClick = { 
                                // Reset values to original
                                itemName = item.itemName
                                itemClassification = item.itemClassificationCode
                                itemType = item.itemClassificationCode
                                originCountry = item.originCountry
                                packagingType = item.packagingUnit
                                quantityType = item.quantityUnit
                                taxCode = item.taxCode
                                barCode = item.barcode ?: ""
                                productCategory = item.itemCategory?.category ?: ""
                                price = item.price

                                // Exit edit mode
                                isEditMode = false 
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color.Transparent,
                                contentColor = MaterialTheme.colors.primary
                            )
                        ) {
                            Text("Cancel")
                        }
                    }

                    // Edit/Save button
                    /*Button(
                        onClick = {
                            if (isEditMode) {
                                // Save changes here
                                // For now, just toggle edit mode
                            }
                            isEditMode = !isEditMode
                        },
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = if (isEditMode) "Save" else "Edit"
                            )
                            Text(if (isEditMode) "Save Changes" else "Edit Item")
                        }
                    }*/
                }
            }
        }
    }
}

@Composable
fun EditableField(
    label: String, 
    value: String, 
    onValueChange: (String) -> Unit,
    isEditable: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.width(240.dp)
) {
    Column(modifier = modifier) {
        Text(
            text = label, 
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = isEditable,
            readOnly = !isEditable,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = if (isEditable) 
                    MaterialTheme.colors.surface 
                else 
                    MaterialTheme.colors.onSurface.copy(alpha = 0.03f),
                disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.09f),
                disabledTextColor = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
            )
        )
    }
}
