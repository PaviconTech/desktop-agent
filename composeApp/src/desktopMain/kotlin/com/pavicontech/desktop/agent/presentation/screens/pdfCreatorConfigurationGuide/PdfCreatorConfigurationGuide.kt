import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import desktopagent.composeapp.generated.resources.Final
import desktopagent.composeapp.generated.resources.Res
import desktopagent.composeapp.generated.resources.SaveSettings
import desktopagent.composeapp.generated.resources.output
import desktopagent.composeapp.generated.resources.profiles
import org.jetbrains.compose.resources.painterResource

@Composable
fun PdfCreatorConfigurationGuide(
    onFinish: () -> Unit
) {
    val steps = listOf(
        StepItem(
            "Welcome", listOf(
                "🛠️ PDFCreator Setup Guide",
                "Follow these steps to configure PDFCreator for automatic PDF saving. This enables the Desktop Agent to automatically generate and track invoices from print actions."
            ), null
        ),
        StepItem(
            imageResource = painterResource(Res.drawable.profiles),
            title = "Open PDFCreator",
            content = listOf(
                "1. Launch PDFCreator.",
                "2. Click **Profiles** in the left sidebar.",
                "3. Select the active profile (e.g., **'Default Profile'** from the dropdown)."
            )
        ),
        StepItem(
            title = "💾 Save Dialog Settings",
            content =listOf(
                "4. Click **Save** to open the settings.",
                "5. Set **Interactive** to **Automatic**.",
                "6. Set **Filename** to `<Title><DateTime:yyyyMMddHHmmss>` (e.g., based on the current date and time: 202506101231).",
                "7. Set **Target directory** to `C:\\Users\\<USER>\\Documents\\DesktopAgent\\InvoiceWatch`.",
                "8. Under **Behavior for existing file**, select **Ensure unique filenames**."
            ),
            imageResource = painterResource(Res.drawable.SaveSettings)
        ),
        StepItem(
            title = "📂 Output Format Settings",
            content = listOf(
                "9. Click **Output format** to open the settings.",
                "10. Ensure **Output format** is set to **PDF**.",
                "11. Set **Color model** to **RGB**.",
                "12. Set **Image compression** to **Automatic** with a factor of 0.66 and resample images to 300 DPI.",
                "13. Set **Page view** to **Single page view** and **Document view** to **Neither document outline nor thumbnail images**.",
                "14. Set **Viewer opens on page** to 1."
            ),
            imageResource = painterResource(Res.drawable.output)
        ),
        StepItem(
            title ="✅ Final Step",
            content=listOf(
                "15. On Actions in Send/Open section click the delete icon to disable PDF Architect pop ups.",
                "16. Click **OK** in both the 'Save' and 'Output format' settings windows to save your profile settings.",
                "17. Click **Save** on the main Profiles page to apply changes.",
                "18. Your system is now ready to print and capture invoices automatically."
            ),
            imageResource = painterResource(Res.drawable.Final)
        )
    )

    var currentStep by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val progress = (currentStep + 1).toFloat() / steps.size.toFloat()
    val step = steps[currentStep]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        // Progress Indicator
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .padding(bottom = 16.dp),
            color = MaterialTheme.colors.primary
        )

        Text(
            text = "Step ${currentStep + 1} of ${steps.size}",
            fontSize = 14.sp,
            color = MaterialTheme.colors.primaryVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =  Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ){

            Column {
                Text(
                    text = step.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                step.content.forEach { line ->
                    Text(
                        text = "• $line",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .padding(vertical = 4.dp)
                    )
                }
            }

            // Display image if available for the current step
            step.imageResource?.let { imageRes ->
                Image(
                    painter = imageRes,
                    contentDescription = "Step ${currentStep + 1} illustration",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(600.dp)
                        .padding(top = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                Button(onClick = { currentStep-- }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(onClick = {
                if (currentStep < steps.lastIndex) {
                    currentStep++
                } else {
                    onFinish()
                }
            }) {
                Text(if (currentStep < steps.lastIndex) "Next" else "Finish")
            }
        }
    }
}

data class StepItem(
    val title: String,
    val content: List<String>,
    val imageResource: Painter?,
)