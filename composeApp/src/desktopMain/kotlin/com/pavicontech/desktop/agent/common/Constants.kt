package com.pavicontech.desktop.agent.common

import java.nio.file.Paths

object Constants {
    //DEMO
    const val ETIMS_BACKEND = "https://sbx.pavicontech.com/api"
    //const val ETIMS_BACKEND = "https://58401cdbb988.ngrok-free.app"
    //LIVE
    //const val ETIMS_BACKEND = "https://erp.pavicontech.com/api"
    const val ETIMS_AI_BACKEND = "https://ai.pavicontech.com/pdf-extractor"
    //DEMO_QR
    const val ETIMS_QR_URL = "https://etims-sbx.kra.go.ke/common/link/etims/receipt/indexEtimsReceiptData?Data="
    //LIVE_QR
    //const val ETIMS_QR_URL = "https://etims.kra.go.ke/common/link/etims/receipt/indexEtimsReceiptData?Data="
    private val HOME_PATH = System.getProperty("user.home")
    val FISCALIZED_RECEIPTS_PATH = Paths.get(HOME_PATH, "Documents", "DesktopAgent", "FiscalizedReceipts")
    val INVOICE_WATCH_FOLDER = Paths.get(HOME_PATH, "Documents", "DesktopAgent", "InvoiceWatch")


    //Live
    //val CONFIG_CAT_KEY="configcat-sdk-1/j7_dCKqm_06Pn7CKUHM0iw/bYn4gNkNCUWp3Fn1IqusWg"
    //DEMO
    const val CONFIG_CAT_KEY="configcat-sdk-1/j7_dCKqm_06Pn7CKUHM0iw/JixiAYMDqkOA-FCMOugj8Q"
    const val VERSION="1.2.8"
    const val INSTALLATION_PROCESS_STATUS = "INSTALLATION_PROCESS_STATUS"
    const val AUTH_TOKEN = "auth_token"
    const val AUTH_TOKEN_EXPIRY = "auth_token_expiry"
    const val SAVED_USER_CREDENTIALS = "saved_user_credentials"
    const val BUSINESS_INFORMATION = "business_information"
    const val PRINT_OUT_OPTIONS = "print_out_options"
    const val LAST_SELECTED_PRINTOUT_INVOICE = "last_selected_printout_invoice"
    const val QR_CODE_COORDINATES = "qr_code_coordinates"
    const val KRA_INFO_COORDINATES = "kra_info_coordinates"
    const val INVOICE_STATUS_FILTER = "invoice_status_filter"


    const val  WATCH_FOLDER = "watch_folder"
    const val SELECTED_PRINTER = "selected_printer"
    const  val ITEM_CODE_LIST = "item_code_list"
    const val PRINTOUT_SIZE = "printout_size"
    const val INVOICE_NO_PREFIX = "invoice_no_prefix"
    const val INVOICE_NO_LIST = "invoice_no_list"
}