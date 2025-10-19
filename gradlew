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
  primary=$(grep '^distributionUrl=' "$PROPERTIES_FILE" | cut -d'=' -f2- | tr -d '\r')
  fallback=$(grep '^distributionUrlFallback=' "$PROPERTIES_FILE" | cut -d'=' -f2- | tr -d '\r')
  urls=''
  if [ -n "$primary" ]; then
    urls=$(printf '%s' "$primary")
  fi
  if [ -n "$fallback" ]; then
    urls=$(printf '%s
%s' "$urls" "$fallback" | sed '/^$/d')
  fi
  if [ -z "$urls" ]; then
    echo "Unable to locate distribution URL in gradle-wrapper.properties"
    exit 1
  fi
  DOWNLOAD_DIR="$BASEDIR/.gradle-wrapper"
  mkdir -p "$DOWNLOAD_DIR"
  for raw_url in $urls; do
    DIST_URL=$(printf '%s' "$raw_url" | sed 's#\\##g')
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
        echo "Download from $DIST_URL failed."
        rm -f "$ZIP_PATH"
        continue
      fi
    fi
    GRADLE_BIN=$(find "$DOWNLOAD_DIR" -maxdepth 2 -type f -name gradle -print -quit)
    if [ -z "$GRADLE_BIN" ]; then
      unzip -q "$ZIP_PATH" -d "$DOWNLOAD_DIR" || {
        echo "Unzip failed for $DIST_URL"
        continue
      }
      GRADLE_BIN=$(find "$DOWNLOAD_DIR" -maxdepth 2 -type f -name gradle -print -quit)
    fi
    if [ -x "$GRADLE_BIN" ]; then
      exec "$GRADLE_BIN" "$@"
    fi
    echo "Gradle binary not found after extracting $DIST_URL"
  done
  echo "All configured Gradle distribution downloads failed; attempting system Gradle fallback."
  run_system_gradle "$@"
}

if [ ! -f "$GRADLE_WRAPPER_JAR" ] || grep -qi "placeholder" "$GRADLE_WRAPPER_JAR"; then
  run_downloaded_gradle "$@"
else
  exec "$JAVA_HOME/bin/java" -jar "$GRADLE_WRAPPER_JAR" "$@"
fi
