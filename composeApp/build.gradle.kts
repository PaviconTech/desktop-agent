import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvmToolchain(17)

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

                // ⚠️ Consider replacing JavaFX 21 with JavaFX 17 to match JVM target
                implementation("org.openjfx:javafx-controls:17")
                implementation("org.openjfx:javafx-web:17")

                implementation(libs.navigation.compose)

                // QR Code Generator + SQLite
                implementation(libs.ktor.qr.code.gen)
                implementation("org.xerial:sqlite-jdbc:3.45.1.0")
                implementation("org.jetbrains.exposed:exposed-core:0.50.1")
                implementation("org.jetbrains.exposed:exposed-dao:0.50.1")
                implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")


                implementation("com.github.hkirk:java-html2image:0.9")            }
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


        jvmArgs += listOf(
            "--add-opens=java.base/sun.misc=ALL-UNNAMED",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-modules=jdk.unsupported"  // <-- add this line
        )

        buildTypes.release.proguard {
            version.set("7.3.2")
            configurationFiles.from(project.file("proguard-rules.pro"))
            isEnabled.set(false)
            obfuscate.set(false)
        }


        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            // 👇 Correct this:
            packageName = "Etims Sync" // Use a simple internal name (lowercase, hyphenated)

            packageVersion = "1.0.0"
            description = "Empower your business with an AI-driven virtual printer that integrates effortlessly with your ERP and KRA eTIMS for smart, compliant invoicing."
            vendor = "Pavicon Technologies"

            modules("java.sql")
            includeAllModules = true
            jvmArgs += listOf(
                "--add-modules=jdk.unsupported,jdk.unsupported.desktop"
            )

            // 👉 Set .ico file for Windows installer and shortcut
            windows {
                menuGroup = "Pavicon Technologies"
                upgradeUuid = "12345678-90ab-cdef-1234-567890abcdef" // change to your UUID
                shortcut = true
                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
            }
        }


        //javaHome = "/usr/lib/jvm/java-17-openjdk-amd64"
        javaHome =  "C:\\Program Files\\Java\\jdk-17"

    }
}
