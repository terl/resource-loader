package co.libly.resourceloader;

/**
 * Library loading mode controls which libraries are attempted to be loaded (installed in the system or bundled
 * in the Lazysodium JAR) and in which order.
 *
 * <p>It is also possible to load a custom build of sodium library from an arbitrary directory using
 * {@link ResourceLoader#load(String, String)}
 */
public enum Mode {

    /**
     * Try to load the system sodium first, if that fails — load the bundled version.
     *
     * <p>This is the recommended mode, because it allows the clients to upgrade the sodium library
     * as soon as it is available instead of waiting for lazysodium release and releasing a new version of
     * the client library/application.
     */
    PREFER_SYSTEM,

    /**
     * Load the bundled version, ignoring the system.
     *
     * <p>This mode might be useful if the system sodium turns out to be outdated and cannot be upgraded.
     */
    BUNDLED_ONLY,

    /**
     * Load the system sodium only, ignoring the bundled.
     *
     * <p>This mode is recommended if it is required to use the system sodium only, and the application
     * must fail if it is not installed.
     */
    SYSTEM_ONLY,
}