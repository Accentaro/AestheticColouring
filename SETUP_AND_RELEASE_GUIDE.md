# Setup & Release Guide (Neon Compose App)

A concise, practical guide to go from fresh clone → local builds → signed release → Play Store.

---

## 1) Prerequisites

- **Android Studio** Hedgehog/Koala or newer
- **JDK 17** (Android Studio’s bundled JDK is fine)
- **Android SDK** (API 34+), build tools, platform tools
- **Device/emulator** for instrumented tests

---

## 2) Clone & First Build

```bash
git clone <your-repo-url>
cd <repo>
# If wrapper JAR is present you can just:
./gradlew tasks
```

If you hit HTTP 403 fetching the Android Gradle Plugin:

- Ensure Offline mode is OFF in Android Studio (Gradle panel → Toggle Offline).
- See `gradle/README.md` for mirrors and wrapper URL fallback notes.
- Make sure corporate proxy (if any) allows *.google.com / Maven Central.

---

## 3) Replace Placeholders (required before release)

| Area | File/Path | What to change |
| --- | --- | --- |
| App Name | app/src/main/res/values/strings.xml | `app_name` |
| Package ID | app/build.gradle.kts & AndroidManifest.xml | Update `applicationId` if you want a custom ID. |
| Colors/Theme | core-ui/.../theme/NeonTheme.kt | Adjust brand neon colors / variants. |
| AdMob IDs | gradle.properties | `ADMOB_APP_ID`, `ADMOB_BANNER_ID`, `ADMOB_INTERSTITIAL_ID` (do not commit real IDs). |
| Billing SKUs | billing/PremiumManager.kt | Create SKUs in Play Console and match IDs here: `premium_unlock_one_time`, `subscription_neon_plus`. |
| Sounds | app/src/main/res/raw/ | Drop your loopable audio files and match names used by SoundscapeEngine (e.g., neon_hum, electric_rain, crystal_echoes). |
| Icons/Graphics | /playstore/assets-placeholders/*.md | Follow instructions, then place actual assets in mipmap-*/ and Play Console listing. |
| Privacy Policy URL | README.md + your site | Add a real URL if you collect data; current app collects none beyond ads/billing SDKs. |
| Localization | app/src/main/res/values[-xx]/strings.xml | Update EN and any additional languages. |

> Tip: Keep real keys only in local gradle.properties (never commit them). Commit the provided gradle.properties.example instead.

---

## 4) Configure Keys (Locally)

Create `gradle.properties` in the project root (same folder as settings.gradle):

```properties
# Example – do not commit these
ADMOB_APP_ID=ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy
ADMOB_BANNER_ID=ca-app-pub-xxxxxxxxxxxxxxxx/aaaaaaaaaa
ADMOB_INTERSTITIAL_ID=ca-app-pub-xxxxxxxxxxxxxxxx/bbbbbbbbbb

# Optional flags
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
org.gradle.caching=true
org.gradle.configuration-cache=true
```

Billing needs no key file, but you must create the products in Play Console with IDs matching code. Use license test accounts for sandbox purchases.

---

## 5) Debug Build

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

Open Dev Tools (tap the main menu logo 5 times):

- Toggle Premium simulate
- Fake audio levels
- Enable FPS overlay
- Reset to defaults

---

## 6) Tests

Unit tests:

```bash
./gradlew test
```

Instrumented tests (requires emulator/device):

```bash
# Start an emulator from Android Studio or AVD Manager
./gradlew connectedCheck
```

If tests fail because no device is connected, ensure an emulator is running and unlocked.

---

## 7) Signing & Release Builds

### 7.1 Create a keystore (one time)

```bash
mkdir -p keystore
keytool -genkey -v -keystore keystore/neon-release.keystore \
  -alias neonKey -keyalg RSA -keysize 2048 -validity 10000
```

Do not commit the keystore. Store passwords in a secure manager.

### 7.2 Configure signing (local only)

Edit `app/build.gradle.kts` signingConfigs release block and set:

```
storeFile = file("keystore/neon-release.keystore")

storePassword, keyAlias, keyPassword (or reference Gradle props)
```

### 7.3 Build release artifacts

```bash
# Unsigned artifacts for CI/local verification:
./gradlew assembleRelease
./gradlew bundleRelease
```

The Play Store requires an AAB:

- Output: `app/build/outputs/bundle/release/app-release.aab`

You may use Play App Signing, which simplifies key handling after first upload.

---

## 8) Play Console Setup

1. Create app → Fill Store Listing (use `/playstore/metadata/en-US/*.txt` as a starting point).
2. Add Billing products with IDs matching code.
3. Add AdMob app and link if desired.
4. Upload `.aab` from `bundleRelease`.
5. Add testers and roll out to internal testing first.
6. Verify in-app purchases on test accounts.

---

## 9) CI (GitHub Actions)

Workflow builds Debug APK and unsigned Release AAB and uploads as artifacts.

To sign in CI:

1. Store `KEYSTORE_BASE64`, `STORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD` in GitHub Secrets.
2. Decode to `keystore/neon-release.keystore` in a CI step.
3. Point signing config to those props (commented example in the workflow).

> The provided workflow intentionally does not sign by default.

---

## 10) Troubleshooting

**AGP 403 / Dependency download issues**

- Disable Offline mode.
- Ensure `google()` and `mavenCentral()` are first in repositories.
- Use the commented mirror examples in `gradle/README.md` temporarily.
- Re-sync Gradle; clear caches if needed.

**App crashes on mic denial**

- Should not happen. If it does, file an issue—Audio features are wrapped with try/catch.

**Ads show while premium**

- Check `pref_premium_unlocked` and ensure AdManager observes it. Clear app data and retry.

---

## 11) Ship Checklist

- [ ] App name, icons, colors updated
- [ ] Sounds placed in `res/raw/` and referenced correctly
- [ ] AdMob IDs in `gradle.properties` (not committed)
- [ ] Billing products created and match code IDs
- [ ] Keystore created and not committed
- [ ] `./gradlew test` and `connectedCheck` pass
- [ ] `bundleRelease` builds a valid `.aab`
- [ ] Store listing text & assets uploaded
- [ ] Internal test rollout verified (ads, billing, audio, settings export/import)
- [ ] Privacy policy URL set if needed

---

## 12) Where to Change Things (Quick Map)

- Theme/Components: `core-ui/`
- Playground: `feature-playground/`
- Calming Loop: `feature-calming/`
- Settings & Backup: `feature-settings/`
- Billing: `billing/`
- Ads: `ads/`
- Audio Engine: `audio/`
- Persistence: `data/`
- Dev Tools & Diagnostics: `ui/debug/`, `common/`

---

## 13) Going Further

- Add Lottie JSON to `assets/lottie/` and wire the hook in `NeonRippleScene`
- Add more locales in `values-<lang>/strings.xml`
- Add Crash/Analytics SDK (replace stubs) if you need telemetry
