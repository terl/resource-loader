package co.libly.resourceloader;

/**
 * A JNA loader, loading the library (if needed) and registering the class native
 * methods.
 *
 * <p>This interface exists to enable unit testing of library loading in a single
 * process — a thing that can only happen once.
 */
public interface JnaLoader {
    void register(Class<?> type, String libLocator);
}