# Trippify Configuration

Update the values below to quickly rebrand or rebalance the experience without digging into code.

## App Identity
- **App Name:** `Trippify`
- **Package Name:** `com.trippify.app`

## Theme Controls
- **Background Color:** `#050505`
- **Primary Accent:** `#FF00FF`
- **Secondary Accent:** `#00FFFF`
- **Tertiary Accent:** `#AA00FF`
- **Glow Intensity:** `0.75`
- **Animation Speed Multiplier:** `1.0`

## Audio Defaults
- **Ambient Audio Enabled:** `true`
- **Haptics Enabled:** `true`

## Monetization
- **Banner Ad Unit ID:** `ca-app-pub-xxxxxxxxxxxxxxxx/banner`
- **Premium SKU:** `premium_unlock`

## Scenes
- **Default Scene ID:** `neon_ripple`
- Drop new scenes into `/app/src/main/java/com/trippify/app/scenes/` and register them in `SceneRegistry.kt`.

> ⚙️ Match these values in `AppConfiguration.kt` when adjusting the configuration programmatically.
