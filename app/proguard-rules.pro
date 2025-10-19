# Core Compose + navigation reflection usage
-keep class androidx.compose.** { *; }
-keep class androidx.activity.ComponentActivity { *; }
-keep class androidx.navigation.** { *; }

# Coroutines/DataStore
-keep class kotlinx.coroutines.** { *; }

# ExoPlayer and billing rely on reflection for service loading.
-keep class com.google.android.exoplayer2.** { *; }
-keep class com.android.billingclient.api.** { *; }

# Hilt / DI annotations (placeholder for future integration)
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn javax.inject.**

# AdMob mediation adapters
-keep class com.google.android.gms.ads.** { *; }

# Activity result contracts used for SAF operations
-keep class androidx.activity.result.contract.ActivityResultContracts$* { *; }

# Retain generated Navigation destinations
-keepclassmembers class * implements androidx.navigation.NamedNavArgument { *; }

# TODO: Audit final dependencies and tune shrinker rules before Play release.
