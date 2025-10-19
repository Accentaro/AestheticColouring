# Gradle Wrapper Notes

The CI environment occasionally blocks direct downloads from `services.gradle.org`, which can manifest as HTTP 403 responses when
resolving the Android Gradle Plugin distribution. If that happens:

1. **Disable offline mode** in Android Studio (Gradle tool window → Toggle Offline).
2. Retry the sync/build with the default repositories (`google()`, `mavenCentral()`).
3. If the block persists, temporarily uncomment one of the mirror URLs in `gradle/wrapper/gradle-wrapper.properties` and re-run
   the build. Remember to switch back to the official distribution once the download succeeds so you track upstream updates.
4. As an alternative, supply the [`gradle/mirror-init.gradle`](../gradle/mirror-init.gradle) init script via
   `./gradlew -I gradle/mirror-init.gradle <task>` to inject network-specific repository mirrors (all entries are commented by
   default; replace with your organisation's endpoints).
4. Clear `~/.gradle/caches` if the distribution cache became corrupt after a partial download.

The wrapper now records a verified SHA-256 for the Gradle distribution. If you change the Gradle version, regenerate the wrapper
locally via:

```bash
./gradlew wrapper --gradle-version <version>
```

and commit the updated `distributionUrl` **and** `distributionSha256Sum`. Never commit the generated JARs from a remote source you
cannot verify—always regenerate locally.

For CI use, the provided workflow enables Gradle caching. To force a clean sync (helpful when mirrors were enabled) run:

```bash
./gradlew --refresh-dependencies tasks
```

from the project root.

Need to revert to the official repositories? Remove any mirror overrides, delete the temporary `GRADLE_OPTS` flag you may have
set for the init script, and run `./gradlew --refresh-dependencies` once to repopulate the cache from Google/MavenCentral.
