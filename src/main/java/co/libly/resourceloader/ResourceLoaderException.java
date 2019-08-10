package co.libly.resourceloader;

/**
 * Indicates a failure to load the required library.
 */
public class ResourceLoaderException extends RuntimeException {

    public ResourceLoaderException(String message) {
        super(message);
    }

    public ResourceLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
