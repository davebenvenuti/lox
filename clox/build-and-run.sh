#!/usr/bin/env bash

set -e

echo "Building clox..."
podman build -t clox:latest --target clox .

echo "Starting clox..."
podman run -it --rm -v $(pwd):/mnt/host clox:latest "$@"
