package com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.sales.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.data.remote.dto.response.getSales.Item
import com.pavicontech.desktop.agent.data.remote.dto.response.signIn.BussinessInfo
import com.pavicontech.desktop.agent.domain.model.Sale
import com.pavicontech.desktop.agent.domain.model.fromBusinessJson
import com.pavicontech.desktop.agent.domain.usecase.fileSysteme.SelectFileUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.GenerateQrCodeUseCase
import com.pavicontech.desktop.agent.domain.usecase.receipt.InsertQrCodeToInvoiceUseCase
import com.pavicontech.desktop.agent.domain.usecase.sales.GenerateQRBitmap
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.settings.components.BoxCoordinates
import com.pavicontech.desktop.agent.presentation.screens.dashboard.screens.status.components.toLocalFormattedString
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject
import java.io.File
import java.nio.file.Paths

@Composable
fun SaleDetailsDialog(
    sale: Sale,
    onDismiss: () -> Unit
) {
    val generateQRBitmap: GenerateQRBitmap = koinInject()
    val selectFileUseCase: SelectFileUseCase = koinInject()
    val scope = rememberCoroutineScope()

    val keyValueStorage: KeyValueStorage = koinInject()

    var pin by remember { mutableStateOf("") }
    var bhfid by remember { mutableStateOf("") }
    var qrUrl by remember { mutableStateOf("") }
    var bindingInvoice by remember { mutableStateOf<File?>(null) }
    var bindedInvoice by remember { mutableStateOf<File?>(null) }
    var viewbindedInvoice by remember { mutableStateOf(false) }


    ViewBindedInvoice(
        file = bindedInvoice,
        show = viewbindedInvoice,
        onDismiss = {viewbindedInvoice = false}
    )


    LaunchedEffect(Unit) {
        keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.let {
            val loaded = it.fromBusinessJson()
            pin = loaded.kraPin
            bhfid = loaded.branchId
            qrUrl = "${Constants.ETIMS_QR_URL}$pin$bhfid${sale.receiptSign}"
        }
    }

    bindingInvoice?.let {
        bindSaleToInvoice(
            inputFile = it,
            businessPin = pin,
            bhfId = bhfid,
            rcptSign = sale.receiptSign ?: "",
            intrlData = sale.intrlData ?: "",
            date = sale.createdAt.toLocalFormattedString(),
            onSuccess = {file ->
                viewbindedInvoice = true
                bindedInvoice = file
            }
        )
    }

    DialogWindow(
        onCloseRequest = onDismiss,
        title = "Sale Details",
        state = rememberDialogState(width = 1200.dp, height = 700.dp),
        resizable = true
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())

        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // Header with title and QR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text("Sale Details", style = MaterialTheme.typography.h5)
                        Text("Invoice No: ${sale.invoiceNumber}", style = MaterialTheme.typography.subtitle2)
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    bindingInvoice = selectFileUseCase.invoke()
                                    bindingInvoice?.let {

                                    }
                                    println(bindingInvoice)
                                }
                            },
                            border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.primary)
                        ){
                            Text(text = "Bind Invoice to Sale")
                        }
                    }
                    Image(
                        bitmap = generateQRBitmap(qrUrl),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Sale and Customer Information
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(32.dp)) {

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sale Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        InfoRow("Reference No.", sale.referenceNumber)
                        InfoRow("ETIMS Receipt", sale.etimsReceiptNumber ?: "-")
                        InfoRow("Status", sale.status, highlight = true)
                        InfoRow("Created At", sale.createdAt.toLocalFormattedString())
                        InfoRow("Items Count", sale.itemsCount.toString())
                        InfoRow("Amount", "KES %.2f".format(sale.amount), highlight = true)
                        InfoRow("Tax", "KES %.2f".format(sale.tax))
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Customer", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1565C0))
                        InfoRow("Name", sale.customerName)
                        InfoRow("KRA PIN", sale.kraPin)
                        InfoRow("Receipt Sign", sale.receiptSign ?: "-")
                        InfoRow("Internal Data", sale.intrlData ?: "-")
                    }
                }

                Spacer(Modifier.height(24.dp))
                SaleItemsTable(sale.items)

                // You can add Sale Items table here if you want

            }
        }
    }
}


@Composable
fun SaleItemsTable(items: List<Item>) {
    Column {
        Text("Sale Items", fontWeight = FontWeight.Bold, fontSize = 16.sp)

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary)
                .padding(vertical = 8.dp)
        ) {
            val headers = listOf(
                "No.", "Item Name", "Desc", "Item Code", "Qty", "Unit", "Price", "Disc Amt", "Tax", "Total"
            )
            headers.forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Table Body
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 400.dp)
        ) {
            itemsIndexed(items) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        "${index + 1}",
                        item.itemNm,
                        item.itemNmDef,
                        item.itemCd,
                        item.qty,
                        item.qtyUnitCd,
                        item.prc,
                        item.dcAmt,
                        item.taxAmt,
                        item.totAmt
                    ).forEach {
                        Text(
                            text = it,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun InfoRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(180.dp)
        )
        Text(
            text = value,
            color = if (highlight) Color(0xFF00C853) else MaterialTheme.colors.onBackground
        )
    }
}

@Composable
 fun bindSaleToInvoice(
    inputFile: File,
    businessPin: String,
    bhfId: String,
    rcptSign:String,
    intrlData:String,
    date:String,
    onSuccess:(File)->Unit = {},
) {

    val scope = rememberCoroutineScope()
    val insertQrCodeToInvoiceUseCase: InsertQrCodeToInvoiceUseCase = koinInject()
    val generateQrCode: GenerateQrCodeUseCase = koinInject()
    val keyValueStorage: KeyValueStorage = koinInject()


    var qrCodeCoordinates by remember { mutableStateOf<BoxCoordinates?>(null) }
    var kraInfoCoordinates by remember { mutableStateOf<BoxCoordinates?>(null) }

    val userHome = System.getProperty("user.home")
    val qrPath = Paths.get(userHome, "Documents", "Receipts", "BindingInvoice", "qr-code.png")

    val receiptDir = Paths.get(userHome, "Documents", "DesktopAgent", "FiscalizedReceipts")
    receiptDir.toFile().mkdirs() // ✅ Ensure directory exists
    val outPutFile = receiptDir.resolve(inputFile.name)


// ✅ Ensure directory exists
    val qrCode = generateQrCode.invoke(
        path = qrPath,
        businessPin = businessPin,
        bhfId = bhfId,
        rcptSign = rcptSign
    )


    val receiptText = """
                        INTRLDATA: $intrlData
                        RCPTSIGN : $rcptSign
                        VSDC DATE: $date
                    """.trimIndent()

    LaunchedEffect(inputFile){

        qrCodeCoordinates = try {
            BoxCoordinates.fromJson(keyValueStorage.get(Constants.QR_CODE_COORDINATES) ?: "")
        } catch (e: Exception) {
            null
        }
        kraInfoCoordinates = try {
            BoxCoordinates.fromJson(keyValueStorage.get(Constants.KRA_INFO_COORDINATES) ?: "")
        } catch (e: Exception) {
            null
        }

        try {
            kraInfoCoordinates?.let { kra ->
                qrCodeCoordinates?.let { qr ->
                    insertQrCodeToInvoiceUseCase.invoke(
                        inputPdf = inputFile,
                        outPutPdf = outPutFile.toFile(),
                        qrCodeImage = qrCode,
                        kraInfoText = receiptText,
                        coordinates = listOf(kra, qr),
                        onSuccess = {
                            onSuccess(outPutFile.toFile())
                            outPutFile.toFile()
                            println("Success Binding")
                        }
                    )
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}

