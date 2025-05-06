import java.io.File
import java.time.LocalDateTime

fun log(msg: String) {
    println("[${LocalDateTime.now()}] $msg")
}

fun runCommand(vararg command: String): String {
    log("Running: ${command.joinToString(" ")}")
    val process = ProcessBuilder(*command)
        .redirectErrorStream(true) // Combine stdout and stderr
        .start()

    // Capture both stdout and stderr and print them to the terminal
    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()
    log(output.trim())  // Output to terminal
    return output.trim()
}

fun isWindows(): Boolean = System.getProperty("os.name").lowercase().contains("win")
fun isLinux(): Boolean = System.getProperty("os.name").lowercase().contains("linux")

fun fileExists(path: String): Boolean = File(path).exists()

fun runCommandAsAdminWindows(command: String): String {
    log("Running as admin: $command")
    val process = ProcessBuilder("runas", "/user:Administrator", command)
        .redirectErrorStream(true)
        .start()

    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()
    log(output.trim())  // Print the output to terminal
    return output.trim()
}

fun installDockerOnWindows() {
    val installerUrl = "https://desktop.docker.com/win/stable/Docker%20Desktop%20Installer.exe"
    val installerPath = File(System.getProperty("java.io.tmpdir"), "docker-installer.exe").absolutePath

    log("Downloading Docker installer for Windows...")
    runCommand("powershell", "-Command", "Invoke-WebRequest -Uri $installerUrl -OutFile $installerPath")

    log("Installing Docker...")
    runCommandAsAdminWindows("cmd /c start $installerPath")

    log("Docker installation complete.")
}

fun installDockerOnLinux() {
    log("Updating package list...")
    runCommand("sudo", "apt", "update")

    log("Installing required packages for Docker...")
    runCommand("sudo", "apt", "install", "-y", "apt-transport-https", "ca-certificates", "curl", "gnupg", "lsb-release")

    log("Adding Docker's official GPG key...")
    runCommand("curl", "-fsSL", "https://download.docker.com/linux/ubuntu/gpg", "|", "sudo", "apt-key", "add", "-")

    log("Setting up stable repository...")
    runCommand("sudo", "add-apt-repository", "\"deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable\"")

    log("Updating package list again...")
    runCommand("sudo", "apt", "update")

    log("Installing Docker...")
    runCommand("sudo", "apt", "install", "-y", "docker-ce", "docker-ce-cli", "containerd.io")

    log("Starting Docker service...")
    runCommand("sudo", "systemctl", "start", "docker")

    log("Enabling Docker to start on boot...")
    runCommand("sudo", "systemctl", "enable", "docker")

    log("Docker installation complete.")
}

fun installMongoDBInDocker() {
    log("Pulling MongoDB Docker image...")
    runCommand("docker", "pull", "mongo")

    log("Running MongoDB container on port 20000 with no authentication...")
    runCommand("docker", "run", "-d", "-p", "20000:27017", "--name", "mongodb", "mongo", "--noauth")

    log("MongoDB is running in Docker on port 20000 with no authentication.")
}

fun main() {
    log("Starting Docker and MongoDB installation...")

    if (isWindows()) {
        // Check if Docker is installed
        val dockerInstalled = runCommand("docker", "--version").contains("Docker version")
        if (!dockerInstalled) {
            installDockerOnWindows()
        } else {
            log("Docker is already installed.")
        }
    } else if (isLinux()) {
        // Check if Docker is installed
        val dockerInstalled = runCommand("docker", "--version").contains("Docker version")
        if (!dockerInstalled) {
            installDockerOnLinux()
        } else {
            log("Docker is already installed.")
        }
    }

    // Once Docker is installed, run MongoDB in Docker
    installMongoDBInDocker()

    log("Docker and MongoDB installation complete.")
}
