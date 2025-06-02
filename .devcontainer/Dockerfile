FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

RUN apt-get update && \
    apt-get install -y curl jq && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Java & Maven are installed via devcontainer "features" in devcontainer.json
