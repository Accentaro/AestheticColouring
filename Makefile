.PHONY: debug release test clean reset

GRADLE=./gradlew

## Build debug APK
debug:
$(GRADLE) assembleDebug

## Build release bundle (unsigned by default)
release:
$(GRADLE) bundleRelease

## Run unit tests
test:
$(GRADLE) test

## Clean project outputs
clean:
$(GRADLE) clean

## Reset local state (clears app DataStore on device + Gradle caches)
reset:
@echo "Clearing Trippify app data on connected device/emulator (ignoring errors if none)."
@ADB=$$(command -v adb); if [ -n "$$ADB" ]; then $$ADB shell pm clear com.trippify.app || true; else echo "adb not found"; fi
@echo "Removing project build cache (.gradle and build folders)."
rm -rf .gradle app/build
@echo "Optionally clear global Gradle caches with 'rm -rf ~/.gradle/caches' (not executed automatically)."
