package org.gradle.api.internal;

/**
 * Compatibility shim for tooling that still references the legacy Gradle
 * {@code HasConvention} type removed in Gradle 8.3+.
 * <p>
 * The Kotlin Gradle plugin bundled with this project only checks for the
 * presence of this interface to wire the conventional source set layout. We
 * simply expose the same signature that used to exist so the plugin can load
 * without throwing {@link ClassNotFoundException} when running on newer Gradle
 * distributions provided by constrained CI environments.
 */
@Deprecated(forRemoval = true)
public interface HasConvention {
    /**
     * Mirrors the historical method signature. Implementations are expected to
     * return Gradle's {@code Convention} object when available.
     */
    org.gradle.api.plugins.Convention getConvention();
}
