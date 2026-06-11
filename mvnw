#!/bin/sh
# Maven Wrapper - Simplified for this project

set -e

# Resolve project base directory
MAVEN_PROJECTBASEDIR="$(cd "$(dirname "$0")" && pwd -P)"
export MAVEN_PROJECTBASEDIR

# Find Java
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Read distribution URL from wrapper properties
WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
DIST_URL=$(grep 'distributionUrl' "$WRAPPER_PROPERTIES" | cut -d'=' -f2- | tr -d '[:space:]')

# Maven install directory
MAVEN_HOME="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/dists/maven"

# Download and extract Maven if not present
if [ ! -d "$MAVEN_HOME" ] || [ -z "$(find "$MAVEN_HOME" -name "mvn" -path "*/bin/mvn" 2>/dev/null)" ]; then
    echo "Downloading Maven distribution..."
    TMPFILE=$(mktemp /tmp/maven-dist.XXXXXX.zip)
    if command -v curl > /dev/null 2>&1; then
        curl -fsSL -o "$TMPFILE" "$DIST_URL"
    elif command -v wget > /dev/null 2>&1; then
        wget -q -O "$TMPFILE" "$DIST_URL"
    else
        echo "ERROR: curl or wget is required to download Maven." >&2
        exit 1
    fi
    mkdir -p "$MAVEN_HOME"
    unzip -qo "$TMPFILE" -d "$MAVEN_HOME"
    rm -f "$TMPFILE"
    echo "Maven downloaded successfully."
fi

# Find the mvn binary inside the extracted distribution
MVN_BIN=$(find "$MAVEN_HOME" -name "mvn" -path "*/bin/mvn" -not -name "*.cmd" | head -1)

if [ -z "$MVN_BIN" ]; then
    echo "ERROR: Cannot find Maven binary after extraction." >&2
    exit 1
fi

chmod +x "$MVN_BIN"

# Execute Maven with the required system property
exec "$MVN_BIN" \
    -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
    "$@"
