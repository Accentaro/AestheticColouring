#!/usr/bin/env sh

BASEDIR=$(cd "$(dirname "$0")" && pwd)
GRADLE_WRAPPER_JAR="$BASEDIR/gradle/wrapper/gradle-wrapper.jar"
PROPERTIES_FILE="$BASEDIR/gradle/wrapper/gradle-wrapper.properties"

run_system_gradle() {
  if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
  fi
  echo "Gradle command not available on PATH."
  exit 1
}

run_downloaded_gradle() {
  DIST_URL=$(grep 'distributionUrl' "$PROPERTIES_FILE" | cut -d'=' -f2- | tr -d '\r')
  DIST_URL=$(printf '%s' "$DIST_URL" | sed 's#\\##g')
  if [ -z "$DIST_URL" ]; then
    echo "Unable to locate distributionUrl in gradle-wrapper.properties"
    exit 1
  fi
  DOWNLOAD_DIR="$BASEDIR/.gradle-wrapper"
  mkdir -p "$DOWNLOAD_DIR"
  ZIP_NAME=$(basename "$DIST_URL")
  ZIP_PATH="$DOWNLOAD_DIR/$ZIP_NAME"
  if [ ! -f "$ZIP_PATH" ]; then
    echo "Downloading Gradle distribution from $DIST_URL"
    STATUS=1
    if command -v curl >/dev/null 2>&1; then
      curl -fL "$DIST_URL" -o "$ZIP_PATH"
      STATUS=$?
    elif command -v wget >/dev/null 2>&1; then
      wget "$DIST_URL" -O "$ZIP_PATH"
      STATUS=$?
    else
      echo "Neither curl nor wget is available to download Gradle."
      run_system_gradle "$@"
    fi
    if [ $STATUS -ne 0 ]; then
      echo "Download failed, falling back to system Gradle if available."
      run_system_gradle "$@"
    fi
  fi
  if [ ! -d "$DOWNLOAD_DIR/gradle" ]; then
    unzip -q "$ZIP_PATH" -d "$DOWNLOAD_DIR" || run_system_gradle "$@"
  fi
  GRADLE_BIN=$(find "$DOWNLOAD_DIR" -maxdepth 2 -type f -name gradle | head -n 1)
  if [ ! -x "$GRADLE_BIN" ]; then
    echo "Failed to locate Gradle binary after download."
    run_system_gradle "$@"
  fi
  exec "$GRADLE_BIN" "$@"
}

if [ ! -f "$GRADLE_WRAPPER_JAR" ] || grep -qi "placeholder" "$GRADLE_WRAPPER_JAR"; then
  run_downloaded_gradle "$@"
else
  exec "$JAVA_HOME/bin/java" -jar "$GRADLE_WRAPPER_JAR" "$@"
fi
