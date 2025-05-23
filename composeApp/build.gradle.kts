import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            val voyagerVersion = "1.1.0-beta02"
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation("cafe.adriel.voyager:voyager-koin:$voyagerVersion")
            implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.filekit.coil)
            implementation(libs.kfswatch)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.engine)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.auth)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screen.model)
            implementation(libs.voyager.koin)
            api(libs.datastore)
            api(libs.datastore.preferences)
            implementation(libs.navigation.compose)
            runtimeOnly(libs.material3.extended)
            implementation(libs.mongodb.core)
            implementation(libs.mongodb.serializer)
            implementation(libs.ktor.qr.code.gen)
            implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10")
            implementation("org.openjfx:javafx-controls:21")
            implementation("org.openjfx:javafx-web:21")


        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

        }
    }
}


compose.desktop {
    application {
        mainClass = "com.pavicontech.desktop.agent.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.pavicontech.desktop.agent"
            packageVersion = "1.0.0"
        }
    }
}
