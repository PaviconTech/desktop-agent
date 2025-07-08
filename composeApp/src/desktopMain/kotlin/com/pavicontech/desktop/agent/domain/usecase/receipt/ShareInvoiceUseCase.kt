import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.model.fromBusinessJson
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ShareInvoiceUseCase(
    private val keyValueStorage: KeyValueStorage
) {

    suspend operator fun invoke(attachment: File?) {
        val businessInfo = keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.fromBusinessJson()

        val subject = "Invoice from ${businessInfo?.name}"
        val body = buildString {
            appendLine("Hello,")
            appendLine()
            appendLine("Please find attached the invoice from ${businessInfo?.name}.")
            appendLine("KRA PIN: ${businessInfo?.kraPin}")
            appendLine()
            appendLine("If you have any questions, feel free to reach out.")
        }

        val recipient = ""

        val os = System.getProperty("os.name").lowercase()

        if (os.contains("win")) {
            val outlookOpened = tryOpenOutlook(subject, body, recipient, attachment)
            if (outlookOpened) return
        }

        // Gmail fallback (works on all OS)
        openGmailCompose(subject, body, recipient)

        if (attachment != null && attachment.exists()) {
            openAttachmentDirectory(attachment)
        } else {
            println("Attachment missing or null.")
        }
    }

    private fun tryOpenOutlook(
        subject: String,
        body: String,
        recipient: String,
        attachment: File?
    ): Boolean {
        val outlookPaths = listOf(
            "C:\\Program Files\\Microsoft Office\\root\\Office16\\OUTLOOK.EXE",
            "C:\\Program Files (x86)\\Microsoft Office\\Office16\\OUTLOOK.EXE",
            "C:\\Program Files\\Microsoft Office\\Office15\\OUTLOOK.EXE"
        )

        val outlook = outlookPaths.firstOrNull { File(it).exists() }
        if (outlook != null && attachment != null && attachment.exists()) {
            try {
                val cmd = arrayOf(
                    outlook,
                    "/c", "ipm.note",
                    "/m", "$recipient&subject=${uriEncode(subject)}&body=${uriEncode(body)}",
                    "/a", attachment.absolutePath
                )
                println("Launching Outlook...")
                Runtime.getRuntime().exec(cmd)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun openGmailCompose(subject: String, body: String, recipient: String) {
        val gmailUrl = "https://mail.google.com/mail/?view=cm&fs=1" +
                "&to=${uriEncode(recipient)}" +
                "&su=${uriEncode(subject)}" +
                "&body=${uriEncode(body)}"

        try {
            println("Opening Gmail compose in browser...")
            Desktop.getDesktop().browse(URI(gmailUrl))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openAttachmentDirectory(file: File) {
        try {
            println("Opening directory: ${file.parentFile}")
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file.parentFile)
            } else {
                println("Desktop not supported on this OS.")
            }
        } catch (e: Exception) {
            println("Failed to open file directory.")
            e.printStackTrace()
        }
    }

    private fun uriEncode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20")
}
