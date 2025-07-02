package com.pavicontech.desktop.agent.common

import java.nio.file.Paths

object Constants {
    //DEMO
    //const val ETIMS_BACKEND = "https://etims.pavicontech.com/api"
    //LIVE
    const val ETIMS_BACKEND = "https://erp.pavicontech.com/api"
    const val ETIMS_AI_BACKEND = "http://172.104.129.116:8005"

    private val HOME_PATH = System.getProperty("user.home")
    val FISCALIZED_RECEIPTS_PATH = Paths.get(HOME_PATH, "Documents", "DesktopAgent", "FiscalizedReceipts")
    val INVOICE_WATCH_FOLDER = Paths.get(HOME_PATH, "Documents", "DesktopAgent", "InvoiceWatch")

    // Local Storage Key Values
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