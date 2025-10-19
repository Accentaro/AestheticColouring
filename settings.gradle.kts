pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // Optional fallback mirror (commented)
        // maven { url = uri("https://maven-mirror.example.com") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // Optional mirror fallback (commented)
        // maven { url = uri("https://maven-mirror.example.com") }
    }
}

rootProject.name = "Trippify"
include(":app")
