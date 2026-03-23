#!/bin/bash

# CrossAI Development Environment Setup Script
# This script sets up the complete development environment

set -e

echo "🚀 Setting up CrossAI development environment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_step() {
    echo -e "${GREEN}[STEP]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check OS
OS=$(uname -s)
print_step "Detected OS: $OS"

# 1. Install JDK 17
print_step "Installing JDK 17..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1-2)
    if [[ "$JAVA_VERSION" == "17" ]]; then
        print_warning "JDK 17 is already installed: $JAVA_VERSION"
    else
        print_warning "Found JDK $JAVA_VERSION, installing JDK 17..."
    fi
else
    print_step "Installing OpenJDK 17..."
    if [[ "$OS" == "Darwin" ]]; then
        brew install openjdk@17
    elif [[ "$OS" == "Linux" ]]; then
        sudo apt update && sudo apt install -y openjdk-17-jdk
    else
        print_error "Unsupported OS for automatic JDK installation"
        exit 1
    fi
fi

print_step "✅ Development environment setup completed!"
echo ""
echo "Next steps:"
echo "1. Restart your terminal or run: source ~/.bashrc"
echo "2. Start Docker daemon"
echo "3. Configure your IDE with the provided settings"