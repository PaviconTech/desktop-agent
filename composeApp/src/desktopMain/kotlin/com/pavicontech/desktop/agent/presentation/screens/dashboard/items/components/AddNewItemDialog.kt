package com.pavicontech.desktop.agent.presentation.screens.dashboard.items.components

/*
 * ────────────────────────────────────────────────────────────────
 *  Add New Item Dialog (Compose Desktop)
 * ────────────────────────────────────────────────────────────────
 *  • Search‑as‑you‑type dropdowns (arrow keys + Enter friendly)
 *  • Enter moves focus to the next input for rapid data entry
 *  • Numeric‑only / decimal‑only enforcement on stock & prices
 *  • Rounded 12 dp surface, roomy paddings, Material‑friendly
 *  • Strong‑typed output via `Item` data class
 * ────────────────────────────────────────────────────────────────
 */

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.pavicontech.desktop.agent.data.local.database.entries.ClassificationCode
import com.pavicontech.desktop.agent.data.remote.dto.request.AddItemReq
import com.pavicontech.desktop.agent.data.remote.dto.response.pullCodesRes.Dtl
import com.pavicontech.desktop.agent.domain.usecase.items.AddItemUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetClassificationCodesUseCase
import com.pavicontech.desktop.agent.domain.usecase.items.GetItemCodesListUseCase
import com.pavicontech.desktop.agent.presentation.screens.dashboard.items.AddItemState
import kotlin.invoke

/* ───────────────────── Dialog Entry ───────────────────── */
@Composable
fun AddNewItemDialog(
    isLoading: Boolean,
    isShown: Boolean,
    onDismissRequest: () -> Unit,
) {



    if (!isShown) return

    DialogWindow(
        onDismissRequest,
        rememberDialogState(width = 1200.dp, height = 740.dp),
        title = "Add New Item",
        resizable = true,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp,
            color = MaterialTheme.colors.surface
        ) {
            AddNewItemForm(
                onAddItem = {

                },
                onCancel = onDismissRequest,
                isLoading = isLoading
            )
        }
    }
}

/* ────────────────────── Form Body ─────────────────────── */
@Composable
private fun AddNewItemForm(
    isLoading: Boolean,
    onAddItem: (AddItemReq) -> Unit,
    onCancel: () -> Unit
) {
    /* Inject use‑cases */
    val getCodes: GetItemCodesListUseCase = koinInject()
    val getClassCodes: GetClassificationCodesUseCase = koinInject()
    val scope = rememberCoroutineScope()

    val addItem:AddItemUseCase = koinInject()
    var addingState by remember { mutableStateOf(AddItemState()) }

    /* Remote data lists */
    val classifications = remember { mutableStateListOf<ClassificationCode>() }
    val taxTypes = remember { mutableStateListOf<Dtl>() }
    val countries = remember { mutableStateListOf<Dtl>() }
    val quantityUnits = remember { mutableStateListOf<Dtl>() }
    val packagingUnits = remember { mutableStateListOf<Dtl>() }
    val itemTypes = remember { mutableStateListOf<Dtl>() }

    /* Fetch once */
    LaunchedEffect(Unit) {
        scope.launch {
            getCodes()?.let { res ->
                fun pull(name: String) =
                    res.clsList.firstOrNull { it.cdClsNm == name }?.dtlList ?: emptyList()
                taxTypes += pull("Taxation Type").distinctBy(Dtl::cd)
                countries += pull("Country").distinctBy(Dtl::cd)
                quantityUnits += pull("Quantity Unit").distinctBy(Dtl::cd)
                packagingUnits += pull("Packing Unit").distinctBy(Dtl::cd)
                itemTypes += pull("Item Type").distinctBy(Dtl::cd)
            }
        }
        scope.launch { getClassCodes()?.let { classifications += it } }
    }

    /* Local state */
    var itemName by remember { mutableStateOf("") }
    var itemCode by remember { mutableStateOf("") }
    var initialStock by remember { mutableStateOf("") }
    var unitPriceRetail by remember { mutableStateOf("") }
    var unitPriceWholesale by remember { mutableStateOf("") }
    var maxStock by remember { mutableStateOf("") }
    var reorderLevel by remember { mutableStateOf("") }
    var lowStockLevel by remember { mutableStateOf("") }

    var selClassification by remember { mutableStateOf<ClassificationCode?>(null) }
    var selCountry by remember { mutableStateOf<Dtl?>(null) }
    var selItemType by remember { mutableStateOf<Dtl?>(null) }
    var selTaxType by remember { mutableStateOf<Dtl?>(null) }
    var selPackagingUnit by remember { mutableStateOf<Dtl?>(null) }
    var selQuantityUnit by remember { mutableStateOf<Dtl?>(null) }

    /* Focus helper */
    val focus = LocalFocusManager.current
    fun Modifier.onEnterNext() = onKeyEvent { key ->
        if (key.key == Key.Enter) {
            focus.moveFocus(FocusDirection.Next); true
        } else false
    }

    /* UI */
    Column(
        Modifier
            .padding(32.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        Arrangement.spacedBy(16.dp)
    ) {
        Text("Create new product or service", style = MaterialTheme.typography.h5)

        /* Row 1 */
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name *") },
                singleLine = true,
                modifier = Modifier.weight(1f).onEnterNext()
            )
            SearchableDropdown(
                selected = selItemType,
                label = "Item Type *",
                options = itemTypes,
                optionLabel = { it.cdNm },
                onSelect = { selItemType = it },
                modifier = Modifier.weight(1f)
            )
        }

        /* Row 2 */
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
            SearchableDropdown(
                selected = selClassification,
                label = "Classification *",
                options = classifications,
                optionLabel = { it.itemClsNm },
                onSelect = { selClassification = it },
                modifier = Modifier.weight(1f)
            )
            SearchableDropdown(
                selected = selCountry,
                label = "Country *",
                options = countries,
                optionLabel = { it.cdNm },
                onSelect = { selCountry = it },
                modifier = Modifier.weight(1f)
            )
        }

        /* Row 3 */
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
            SearchableDropdown(
                selected = selTaxType,
                label = "Tax Type *",
                options = taxTypes,
                optionLabel = { it.cdNm },
                onSelect = { selTaxType = it },
                modifier = Modifier.weight(1f)
            )
            SearchableDropdown(
                selected = selPackagingUnit,
                label = "Packaging Unit *",
                options = packagingUnits,
                optionLabel = { it.cdNm },
                onSelect = { selPackagingUnit = it },
                modifier = Modifier.weight(1f)
            )
            SearchableDropdown(
                selected = selQuantityUnit,
                label = "Quantity Unit *",
                options = quantityUnits,
                optionLabel = { it.cdNm },
                onSelect = { selQuantityUnit = it },
                modifier = Modifier.weight(1f)
            )
        }

        /* Row 4 */
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = itemCode,
                onValueChange = { itemCode = it },
                label = { Text("Barcode") },
                singleLine = true,
                modifier = Modifier.weight(1f).onEnterNext()
            )
            OutlinedTextField(
                value = initialStock,
                onValueChange = { initialStock = it.filter(Char::isDigit) },
                label = { Text("Initial Stock") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).onEnterNext()
            )
        }

        /* Row 5 */
        Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = unitPriceRetail,
                onValueChange = { unitPriceRetail = it.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text("Price (Retail)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).onEnterNext()
            )
        }


        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), Arrangement.End) {
            if (addingState.isLoading) CircularProgressIndicator() else {
                Button(
                    onClick = {
                        scope.launch {
                            println("loading")
                            addingState = AddItemState(isLoading = true)
                            val res = addItem.invoke(
                                AddItemReq(
                                    itemName = itemName,
                                    itemClassificationCode = selClassification?.itemClsCd ?: "",
                                    itemStdNm = itemName,
                                    itemType = selItemType?.cd ?: "",
                                    originCountry = selCountry?.cd ?: "",
                                    packagingUnit = selPackagingUnit?.cd ?: "",
                                    quantityUnit = selQuantityUnit?.cd ?: "",
                                    taxCode = selTaxType?.cd ?: "",
                                    price = unitPriceRetail,
                                    currentStock = initialStock.toIntOrNull() ?: 0
                                )
                            )
                            println("res: $res")
                            addingState = AddItemState(isLoading = false, isSuccessful = res?.status ?: false)

                        }
                    }
                ) { Text("Add Item") }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> SearchableDropdown(
    selected: T?,
    label: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var userIsTyping by remember { mutableStateOf(false) }

    // Filter options as you type
    val filteredOptions = remember(query, options) {
        if (query.isBlank()) options
        else options.filter { optionLabel(it).contains(query, ignoreCase = true) }
    }

    // Show query if typing, or selected item label
    val displayText = if (userIsTyping) query else selected?.let(optionLabel) ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
            if (it) userIsTyping = true
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {
                query = it
                userIsTyping = true
                expanded = true
            },
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    if (it.isFocused) {
                        expanded = true
                        userIsTyping = true
                        if (query.isEmpty() && selected != null) {
                            query = optionLabel(selected)
                        }
                    }
                }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                userIsTyping = false
                query = selected?.let(optionLabel) ?: ""
            }
        ) {
            if (filteredOptions.isEmpty()) {
                DropdownMenuItem(onClick = {}) {
                    Text("No results")
                }
            } else {
                filteredOptions.forEach { item ->
                    DropdownMenuItem(onClick = {
                        onSelect(item)
                        query = optionLabel(item)
                        userIsTyping = false
                        expanded = false
                    }) {
                        Text(optionLabel(item))
                    }
                }
            }
        }
    }
}


/* ──────────────────── Data Class ─────────────────────── */

data class Item(
    val name: String,
    val classificationCode: String,
    val countryOfOrigin: String,
    val itemType: String,
    val taxCode: String,
    val packagingType: String,
    val quantityUnit: String,
    val code: String,
    val initialStock: Int,
    val unitPriceRetail: Double,
    val unitPriceWholesale: Double,
    val maxStock: Int,
    val reorderLevel: Int,
    val lowStockLevel: Int
)


