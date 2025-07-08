import com.pavicontech.desktop.agent.common.Constants
import com.pavicontech.desktop.agent.data.local.cache.KeyValueStorage
import com.pavicontech.desktop.agent.domain.model.BusinessInformation
import com.pavicontech.desktop.agent.domain.model.fromBusinessJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit
import kotlin.Result

class ShareInvoiceUseCase(
    private val keyValueStorage: KeyValueStorage
) {

    suspend operator fun invoke(attachment: File?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val businessInfo = keyValueStorage.get(Constants.BUSINESS_INFORMATION)?.fromBusinessJson()

            // Validate business info
            if (businessInfo?.name.isNullOrBlank()) {
                return@withContext Result.failure(IllegalStateException("Business information not available"))
            }

            val recipient = "" // TODO: Should be passed as parameter or configured
            val subject = createSubject(businessInfo.name!!)
            val body = createEmailBody(businessInfo)

            val emailSent = when {
                isWindows() -> tryOpenOutlook(subject, body, recipient, attachment)
                else -> false
            }

            if (!emailSent) {
                openGmailCompose(subject, body, recipient)
                attachment?.let { openAttachmentDirectoryIfExists(it) }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isWindows(): Boolean =
        System.getProperty("os.name").lowercase().contains("win")

    private fun createSubject(businessName: String): String =
        "Invoice from $businessName"

    private fun createEmailBody(businessInfo: BusinessInformation): String =
        buildString {
            appendLine("Hello,")
            appendLine()
            appendLine("Please find attached the invoice from ${businessInfo.name}.")
            businessInfo.kraPin?.let { appendLine("KRA PIN: $it") }
            appendLine()
            appendLine("If you have any questions, feel free to reach out.")
        }

    private suspend fun tryOpenOutlook(
        subject: String,
        body: String,
        recipient: String,
        attachment: File?
    ): Boolean = withContext(Dispatchers.IO) {
        if (attachment?.exists() != true) return@withContext false

        try {
            val scriptFile = File.createTempFile("outlook_mail", ".ps1")
            val script = createPowerShellScript(subject, body, recipient, attachment)

            scriptFile.writeText(script)

            val process = ProcessBuilder(
                "powershell.exe",
                "-ExecutionPolicy", "Bypass",
                "-File", scriptFile.absolutePath
            )
                .redirectErrorStream(true)
                .start()

            val success = process.waitFor(30, TimeUnit.SECONDS) && process.exitValue() == 0

            // Clean up temp file
            scriptFile.delete()

            success
        } catch (e: Exception) {
            false // Log this in production
        }
    }

    private fun createPowerShellScript(
        subject: String,
        body: String,
        recipient: String,
        attachment: File
    ): String = """
        try {
            '$'Outlook = New-Object -ComObject Outlook.Application
            '$'mail = '$'Outlook.CreateItem(0)
            '$'mail.Subject = "${escapeForPowerShell(subject)}"
            '$'mail.Body = "${escapeForPowerShell(body)}"
            '$'mail.To = "$recipient"
            '$'mail.Attachments.Add("${attachment.absolutePath.replace("\\", "\\\\")}")
            '$'mail.Display()
            exit 0
        } catch {
            Write-Error '$'_.Exception.Message
            exit 1
        }
    """.trimIndent()

    private fun openGmailCompose(subject: String, body: String, recipient: String) {
        val gmailUrl = buildString {
            append("https://mail.google.com/mail/?view=cm&fs=1")
            append("&to=${uriEncode(recipient)}")
            append("&su=${uriEncode(subject)}")
            append("&body=${uriEncode(body)}")
        }

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI(gmailUrl))
            }
        } catch (e: Exception) {
            // Log error in production - don't print to console
            throw RuntimeException("Failed to open Gmail compose window", e)
        }
    }

    private fun openAttachmentDirectoryIfExists(file: File) {
        val parentDir = file.parentFile
        if (parentDir?.exists() == true) {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(parentDir)
                }
            } catch (e: Exception) {
                // Log error in production
                throw RuntimeException("Failed to open attachment directory: ${parentDir.absolutePath}", e)
            }
        }
    }

    private fun uriEncode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)
            .replace("+", "%20")

    private fun escapeForPowerShell(text: String): String =
        text.replace("`", "``")      // Escape backticks
            .replace("\"", "`\"")    // Escape double quotes
            .replace("$", "`$")      // Escape dollar signs
}