import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm("desktop")

    val sqllinVersion = "1.4.2"

    sourceSets {
        val commonMain by getting {
            dependencies {
                val voyagerVersion = "1.1.0-beta02"

                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // AndroidX + Voyager
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation("cafe.adriel.voyager:voyager-koin:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-screenmodel:$voyagerVersion")
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screen.model)
                implementation(libs.voyager.koin)

                // Koin
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose.viewmodel)

                // FileKit, MongoDB, QR, Ktor
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
                implementation("com.openhtmltopdf:openhtmltopdf-pdfbox:1.0.10")
                implementation("org.openjfx:javafx-controls:21")
                implementation("org.openjfx:javafx-web:21")

                // DataStore
                api(libs.datastore)
                api(libs.datastore.preferences)
                implementation(libs.navigation.compose)

                // MongoDB
                implementation(libs.mongodb.core)
                implementation(libs.mongodb.serializer)

                // QR Code Generator
                implementation(libs.ktor.qr.code.gen)
                implementation("org.xerial:sqlite-jdbc:3.45.1.0") // latest version as of 2025
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
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
