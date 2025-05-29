package com.pavicontech.desktop.agent.common

import java.nio.file.Paths

object Constants {
    const val ETIMS_BACKEND = "https://etims.pavicontech.com/api"
    //const val ETIMS_BACKEND = "https://6815-197-248-204-65.ngrok-free.app/"
    const val ETIMS_AI_BACKEND = "http://172.104.129.116:8005"

    private val HOME_PATH = System.getProperty("user.home")
    val FISCALIZED_RECEIPTS_PATH = Paths.get(HOME_PATH, "Documents", "DesktopAgent", "FiscalizedReceipts")

    // Local Storage Key Values
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
}