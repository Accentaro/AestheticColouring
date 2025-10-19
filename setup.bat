@echo off
setlocal ENABLEDELAYEDEXPANSION

echo === Trippify Development Setup (Windows) ===

echo.
if not exist "gradle.properties" (
  if exist "gradle.properties.example" (
    copy /Y gradle.properties.example gradle.properties >nul
    echo Created gradle.properties from template. Update AdMob/Billing IDs before release.
  ) else (
    echo gradle.properties.example missing. Please recreate from repository history.
  )
) else (
  echo gradle.properties already present ^- leaving untouched.
)

echo.
if exist keystore (
  for %%K in (keystore\*.keystore) do (
    set FOUND_KEYSTORE=1
  )
)
if not defined FOUND_KEYSTORE (
  echo ^>^> WARNING: Keystore not found ^- release builds will remain unsigned until you add one. See SETUP_AND_RELEASE_GUIDE.md.
) else (
  echo Found keystore directory. Keep credentials out of version control.
)

echo.
echo Next steps:
echo   * Run gradlew.bat assembleDebug (or "make debug" from WSL) to confirm the build.
echo   * If Gradle downloads fail, review gradle\mirror-init.gradle for mirror guidance.
echo   * Launch an emulator/device before running gradlew.bat connectedCheck for UI tests.

echo Setup complete. Happy building!
endlocal
