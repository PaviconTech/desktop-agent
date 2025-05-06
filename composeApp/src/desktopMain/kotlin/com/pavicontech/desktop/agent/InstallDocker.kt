import java.io.*
import java.time.LocalDateTime

fun log(msg: String) {
    println("[${LocalDateTime.now()}] $msg")
}

fun runCommand(vararg command: String): String {
    log("Running: ${command.joinToString(" ")}")
    val process = ProcessBuilder(*command)
        .redirectErrorStream(true)
        .start()

    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()
    log(output)
    return output
}

fun isWindows(): Boolean = System.getProperty("os.name").lowercase().contains("win")
fun isLinux(): Boolean = System.getProperty("os.name").lowercase().contains("linux")

fun dockerExists(): Boolean {
    return try {
        runCommand("docker", "--version").contains("Docker version")
    } catch (e: Exception) {
        false
    }
}

fun installDockerOnLinux(password: String) {
    runCommand("sudo", "-S", "apt", "update")
    runCommand("sudo", "-S", "apt", "install", "-y", "ca-certificates", "curl", "gnupg", "lsb-release")

    val keyringDir = "/etc/apt/keyrings"
    File(keyringDir).mkdirs()

    runCommand(
        "bash", "-c", """
        curl -fsSL https://download.docker.com/linux/ubuntu/gpg | 
        gpg --dearmor -o $keyringDir/docker.gpg
        """.trimIndent()
    )

    runCommand(
        "bash", "-c", """
        echo "deb [arch=$(dpkg --print-architecture) signed-by=$keyringDir/docker.gpg] \
        https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
        """.trimIndent()
    )

    runCommand("sudo", "-S", "apt", "update")
    runCommand("sudo", "-S", "apt", "install", "-y", "docker-ce", "docker-ce-cli", "containerd.io")

    log("Starting Docker...")
    runCommand("sudo", "systemctl", "start", "docker")
    runCommand("sudo", "systemctl", "enable", "docker")
}

fun elevateWindows(): Boolean {
    val script = """
        $'currentIdentity' = [Security.Principal.WindowsIdentity]::GetCurrent()
        $'principal' = New-Object Security.Principal.WindowsPrincipal($'currentIdentity')
        if ($'principal'.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
            # Running as Administrator, proceed with Docker installation
            exit
        } else {
            # Not an administrator, prompt to restart with elevated permissions
            Start-Process -Verb RunAs -FilePath "java" -ArgumentList "-cp", ".", "InstallDockerKt"
            exit
        }
    """.trimIndent()

    val tempScript = File.createTempFile("elevate", ".ps1")
    tempScript.writeText(script)
    runCommand("powershell", "-ExecutionPolicy", "Bypass", "-File", tempScript.absolutePath)
    return true
}


fun installDockerOnWindows() {
    val installerUrl = "https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe"
    val installerPath = File(System.getProperty("java.io.tmpdir"), "DockerInstaller.exe").absolutePath

    log("Downloading Docker installer...")
    runCommand("powershell", "-Command", "Invoke-WebRequest -Uri '$installerUrl' -OutFile '$installerPath'")

    log("Running installer silently...")
    runCommand("powershell", "-Command", "Start-Process -FilePath '$installerPath' -ArgumentList '/quiet' -Wait")
}

fun main() {
    log("Checking if Docker is installed...")
    if (dockerExists()) {
        log("Docker is already installed.")
        return
    }

    if (isLinux()) {
        val console = System.console()
        val password = console?.readPassword("Enter sudo password: ")?.joinToString("") ?: ""
        installDockerOnLinux(password)
    } else if (isWindows()) {
        elevateWindows() // Will re-launch with admin rights if not elevated
        installDockerOnWindows()
    } else {
        log("Unsupported OS.")
    }

    log("Docker installation complete.")
}
