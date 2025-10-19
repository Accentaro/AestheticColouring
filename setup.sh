#!/usr/bin/env bash
set -euo pipefail

printf '\n=== Trippify Development Setup ===\n\n'

if [ ! -f "gradle.properties" ]; then
  if [ -f "gradle.properties.example" ]; then
    cp gradle.properties.example gradle.properties
    echo "Created gradle.properties from template. Update AdMob/Billing IDs before release."
  else
    echo "gradle.properties.example missing. Please recreate from repository history." >&2
  fi
else
  echo "gradle.properties already present – leaving untouched."
fi

if [ -d "keystore" ] && find keystore -maxdepth 1 -type f -name '*.keystore' >/dev/null 2>&1; then
    echo "Keystore directory detected. Remember to keep credentials out of version control."
else
    echo "⚠️  Keystore not found — release builds will remain unsigned until you create one (see SETUP_AND_RELEASE_GUIDE.md)."
fi

echo "\nNext steps:"
echo "  • Run ./gradlew assembleDebug (or 'make debug') to verify the project builds."
echo "  • Review gradle/mirror-init.gradle if Gradle downloads are blocked by your network."
echo "  • Use ./gradlew connectedCheck once an emulator/device is running for UI tests."

echo "Setup complete. Happy building!"
