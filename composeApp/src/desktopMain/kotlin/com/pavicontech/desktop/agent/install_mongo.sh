#!/bin/bash

# Function to check if Docker is installed
check_docker_installed() {
    if ! command -v docker &> /dev/null
    then
        echo "Docker is not installed. Installing Docker..."
        install_docker
    else
        echo "Docker is already installed."
        start_docker
    fi
}

# Function to install Docker
install_docker() {
    # Update the apt package index
    echo "Updating apt package index..."
    sudo apt-get update -y

    # Install required packages for Docker
    echo "Installing required packages..."
    sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common

    # Add Docker's official GPG key
    echo "Adding Docker's GPG key..."
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

    # Set up Docker stable repository
    echo "Setting up Docker repository..."
    sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

    # Install Docker CE (Community Edition)
    echo "Installing Docker..."
    sudo apt-get update -y
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io

    # Start Docker service
    start_docker
}

# Function to start Docker service
start_docker() {
    echo "Starting Docker service..."
    sudo systemctl start docker
    sudo systemctl enable docker
    echo "Docker is now running."
}

# Pull MongoDB Docker image
pull_mongo_image() {
    echo "Pulling MongoDB Docker image..."
    docker pull mongo
}

# Run MongoDB container
run_mongo_container() {
    echo "Running MongoDB container..."
    docker run -d --name mongodb -p 27017:27017 mongo
}

# Main installation process
main() {
    check_docker_installed
    pull_mongo_image
    run_mongo_container
    echo "MongoDB container is up and running."
}

# Execute the main function
main
