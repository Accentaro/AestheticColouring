# Trippify

Trippify is an immersive Android experience built with Jetpack Compose that blends neon visuals, responsive haptics, ambient audio, and respectful monetization. The current repository captures the production-ready foundation requested for Stage 2 of the project brief: ripple physics, audio-reactive rendering, premium gating, persistent settings, and calming soundscape loops.

## Feature Overview

| Area | Free Tier | Premium Enhancements |
| --- | --- | --- |
| Neon Ripple Playground | Touch-duration and velocity sensitive ripples, optional trails & audio reactive glow | Multicolour palettes, advanced trail tuning hooks |
| Calming Loops | Neon Hum soundscape with fade in/out and master volume | Electric Rain & Crystal Echoes loops |
| Settings | DataStore-backed toggles for haptics, audio, trails, audio reactive mode, default scene, ads, JSON backup/export | Premium badge, ads disabled automatically, developer tools access |
| Monetization | Banner ads on menu/settings, in-app premium CTA | Purchase simulation hooks & future Billing integration |

Additional highlights:

* Audio-reactive `NeonAudioEngine` exposes low/mid/high normalized energy levels with a resilient synthetic fallback when microphone access is denied.
* `SoundscapeEngine` uses ExoPlayer with soft cross-fades and volume automation for calming loops.
* Jetpack DataStore persistence through `SettingsStore` keeps user preferences across process death and reinstalls (when imported/exported in future updates).
* Premium state is centrally managed through `PremiumManager`, allowing simulated unlocks during development and clear TODOs for BillingClient integration.

## Project Structure

```
app/
├── src/main/java/com/trippify/app/
│   ├── ads/              # AdMob wrappers and Compose banner
│   ├── audio/            # Ambient audio, neon analyzer, and soundscape engines
│   ├── billing/          # Premium state manager & billing stubs
│   ├── core/             # Theme, configuration, navigation, and shared app state
│   ├── data/             # DataStore-backed SettingsStore
│   ├── scenes/           # Scene registry + Neon Ripple implementation
│   └── ui/               # Compose screens & reusable neon components
├── src/main/res/         # Strings, colors, and launcher assets
└── build.gradle.kts      # Module configuration & dependencies
```

## Getting Started

1. Open the project in Android Studio Giraffe (or newer) with Kotlin 1.9+ support.
2. Run `./gradlew tasks` once to download dependencies (the wrapper script depends on the local `gradle-wrapper.jar`).
3. Create a `gradle.properties` (see `gradle.properties.example`) with your AdMob IDs and billing SKUs before shipping.
4. Build and deploy on a device/emulator running Android 8.0 (API 26) or above.
5. Review [`SETUP_AND_RELEASE_GUIDE.md`](SETUP_AND_RELEASE_GUIDE.md) for the detailed local setup, signing, and Play Store release workflow.

### 🚧 If Gradle fails with 403 or cannot download plugins — do this:

1. Disable Gradle's offline mode in Android Studio (Gradle tool window → Toggle Offline).
2. Re-run `./gradlew --refresh-dependencies` to retry with the official `google()`/`mavenCentral()` repositories.
3. If your network blocks Google CDNs, uncomment the fallback URL in [`gradle/wrapper/gradle-wrapper.properties`](gradle/wrapper/gradle-wrapper.properties).
4. For air-gapped or proxied environments, invoke `./gradlew -I gradle/mirror-init.gradle <task>` and replace the commented mirror entries with your internal repository endpoints.
5. Once the build succeeds, revert to the official sources so future Gradle upgrades inherit security patches automatically.

### Runtime Behaviour

* `MainActivity` enforces immersive mode and launches the Compose app shell.
* `TrippifyApp` reads persisted preferences, drives navigation, and orchestrates audio/ads/premium flows.
* `VisualPlaygroundScreen` hosts the upgraded Neon Ripple scene with particle trails, haptic life-cycle pulses, and audio-reactive glow intensity.
* `CalmingLoopScreen` lets users pick soundscapes, toggle playback, and adjust a neon-themed master volume slider.

## Development Notes

* The ripple effect responds to touch duration, pointer velocity, and optional microphone energy. Trail particles linger with afterglow decay to emphasise motion.
* `SettingsScreen` uses reusable `NeonToggle` components with accessibility semantics, inline descriptions, premium lock messaging, and JSON backup/restore controls.
* Ads are automatically disabled when premium is unlocked; debug builds expose a simulated unlock toggle to assist QA.
* `SoundscapeEngine` expects real loopable audio under `app/src/main/res/raw/` with URIs such as `asset://neon_hum`. TODO markers highlight where to inject final assets.
* `PremiumManager.launchPremiumFlow` currently simulates success—replace with BillingClient logic before release.
* Hidden developer tools (tap the logo five times on the main menu) provide premium overrides and audio-reactive simulation toggles for QA.

## Build & Test

```bash
./gradlew assembleDebug
./gradlew test        # Unit tests (to be expanded alongside new features)
```

The GitHub Actions workflow (`.github/workflows/android-build.yml`) runs `./gradlew test` and assembles the debug APK on each push/PR.

## Phase 2 Development Roadmap

1. **Audio Expansion** – Replace the synthetic fallback in `NeonAudioEngine` with Visualizer/AudioRecord pipelines, add permission-aware onboarding, and surface live spectrum visualisations.
2. **Creative Toolkit** – Implement the Coloring Zone palette editor with DataStore export/import, allowing ripple scenes to read user gradients in real time.
3. **Monetization Hardening** – Wire BillingClient purchase flows, receipt validation, and a premium receipt cache. Introduce subscription handling for `subscription_neon_plus`.
4. **Accessibility & Internationalisation** – Finalize multi-language strings, high-contrast theme toggles, and dynamic font scaling smoke tests.
5. **CI & QA** – Add Detekt, ktlint, and Compose UI tests (Settings persistence, Calming Loop playback, Premium gating) to GitHub Actions with Gradle caching.

## Privacy & Permissions

* The app requests microphone access only when the user enables audio-reactive glow. A fallback synthetic analyzer ensures visuals continue gracefully if permission is denied.
* Ads use AdMob banner placements on non-immersive screens; no personal data is collected within this scaffold.

## How to add real AdMob/Billing keys

1. Copy `gradle.properties.example` to `~/.gradle/gradle.properties` or the project root `gradle.properties`.
2. Populate `ADMOB_APP_ID`, `ADMOB_BANNER_ID`, `ADMOB_INTERSTITIAL_ID`, and billing SKU IDs with your production values.
3. Reference them inside `app/build.gradle.kts` via `BuildConfig` or `manifestPlaceholders` when wiring the real SDKs.
4. Never commit real keys—keep them in your local/CI secrets stores.

## How to generate a release keystore

```bash
keytool -genkeypair \
  -alias trippifyRelease \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -keystore trippify-release.keystore
```

Store the generated keystore outside version control (for example `android/keystores/`). Update your local `gradle.properties` with:

```
RELEASE_STORE_FILE=/absolute/path/to/trippify-release.keystore
RELEASE_STORE_PASSWORD=change-me
RELEASE_KEY_ALIAS=trippifyRelease
RELEASE_KEY_PASSWORD=change-me
```

## 🚀 Publishing Checklist

- [ ] Add your secure keystore (see instructions above) and set the signing credentials via `gradle.properties`.
- [ ] Replace placeholder AdMob/Billing IDs with production values stored outside of version control.
- [ ] Update `/playstore/metadata/en-US/*.txt` with final marketing copy and privacy disclosures.
- [ ] Swap placeholder icons/audio/Lottie assets with production-ready files (see "Assets to replace").
- [ ] Generate signed binaries: `./gradlew assembleRelease bundleRelease` (AAB is required for Play Console uploads; APKs are optional for sideload/testing).
- [ ] Verify `proguard-rules.pro` keeps required billing/ads classes and run smoke tests on a release build.

> **Warning:** Do **not** upload a debug build. Google Play Console requires a signed release `.aab`, while `.apk` artifacts are primarily for local installs and QA.

### Common Play Console Errors & Fixes

| Error | Likely Cause | Quick Fix |
| --- | --- | --- |
| *Upload failed: You uploaded an APK or Android App Bundle that was not signed with a valid certificate.* | Using the debug keystore or missing signing config. | Rebuild with the release keystore configured in `app/build.gradle.kts` and `gradle.properties`. |
| *Missing privacy policy URL* | Listing lacks required privacy policy even if no data is collected. | Host a lightweight privacy statement (e.g., GitHub Pages) and paste the URL into the Play Console form. |
| *Advertising ID declaration required* | AdMob placeholders exist but the declaration form is empty. | Complete the Ads section in Data Safety, noting banner usage only on menu/settings screens. |
| *Unoptimized expansion file warning* | Uploading only an APK without bundle or missing 64-bit support. | Prefer the `.aab`; ensure `arm64-v8a` is included (handled automatically by the Gradle config). |

## Test checklist before release

- [ ] `./gradlew test` passes locally and in CI.
- [ ] Manual run-through of Neon Playground (haptics, trails, audio-reactive) on at least two Android API levels.
- [ ] Calming Loop playback cross-fades correctly and premium-gated loops remain locked.
- [ ] Settings export/import restores user preferences across reinstall.
- [ ] Developer Tools override behaves as expected and is hidden from casual users.
- [ ] Premium purchase simulation toggles reset to real entitlements before shipping.

## Assets to replace

| Path | Description |
| --- | --- |
| `app/src/main/res/mipmap-*` | Final adaptive icons and round icons. |
| `app/src/main/res/drawable/` | Splash/background art and any custom drawable accents. |
| `app/src/main/res/raw/` | Loopable audio files for Neon Hum, Electric Rain, Crystal Echoes. |
| `assets/lottie/neon_pulse.json` | Production Lottie animation for ripple overlays. |
| `app/src/main/res/values/strings.xml` | Marketing copy, localized strings, and content descriptions. |
| `docs/preview/` | Drop marketing screenshots and animated previews (see `docs/preview/README.md`). |

---

Enjoy crafting trippy sensory experiences! 🌌
