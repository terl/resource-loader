package co.libly.resourceloader;

import co.libly.resourceloader.mode.BundledMode;
import co.libly.resourceloader.mode.ResourceLoaderMode;
import co.libly.resourceloader.mode.SystemMode;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SharedLibraryLoader extends ResourceLoader {

    protected final JnaLoader loader;

    public SharedLibraryLoader(JnaLoader loader) {
        super();
        this.loader = loader;
    }

    public File load(List<ResourceLoaderMode> modes, List<Class> classesToBind) {
        if (modes.size() == 0) {
            return null;
        }
        int errorCount = 0;
        synchronized (lock) {
            for (int i = 0; i < modes.size(); i++) {
                ResourceLoaderMode mode = modes.get(i);

                // If the user wants to load the library from the system
                // let's try to fulfill their request
                if (mode instanceof SystemMode) {
                    try {
                        SystemMode systemMode = (SystemMode) mode;
                        loadSystemLibrary(systemMode.getLibraryName(), classesToBind);
                        return null;
                    } catch (Exception e) {
                        String error = String.format("Attempt %s loading from system %s", i, e.getMessage());
                        IOException newError = new IOException(error);
                        newError.printStackTrace();
                        errorCount++;
                    }
                }

                // If the user wants to load from inside
                // then let's try do that too
                if (mode instanceof BundledMode) {
                    try {
                        BundledMode bundledMode = (BundledMode) mode;
                        return loadBundledLibrary(bundledMode.getRelativePath(), classesToBind);
                    } catch (Exception e) {
                        String error = String.format("Attempt %s loading from JAR %s", i, e.getMessage());
                        IOException newError = new IOException(error);
                        newError.printStackTrace();
                        errorCount++;
                    }
                }
            }
        }

        // If the error counts is equal to the same size as
        // the modes list, it means we exhausted every mode.
        if (errorCount == modes.size()) {
            IOException ioException = new IOException("Could not load your shared library from JAR or system.");
            throw new UncheckedIOException(ioException);
        }

        return null;
    }

    public void loadSystemLibrary(String libraryName, Class clzz) {
        loadSystemLibrary(libraryName, Collections.singletonList(clzz));
    }

    public void loadSystemLibrary(String libraryName, List<Class> classes) {
        registerLibraryWithClasses(libraryName, classes);
    }

    public File loadBundledLibrary(String relativePath, Class clzz) {
        return loadBundledLibrary(relativePath, Collections.singletonList(clzz));
    }

    public File loadBundledLibrary(String relativePath, List<Class> classes) {
        try {
            File library = copyFromJarToTemp(relativePath, null, new HashSet<>());
            if (library.isDirectory()) {
                throw new IOException("Please supply a relative path to a file and not a directory.");
            }
            registerLibraryWithClasses(library.getAbsolutePath(), classes);
            requestDeletion(library);
            return library;
        } catch (IOException e) {
            String message = String.format(
                    "Failed to load the bundled library from resources by relative path (%s)",
                    relativePath
            );
            throw new ResourceLoaderException(message, e);
        }
    }

    private void registerLibraryWithClasses(String absolutePath, List<Class> classes) {
        requireNonNull(absolutePath, "Please supply an absolute path.");
        synchronized (lock) {
            try {
                for (Class clzz : classes) {
                    loader.register(clzz, absolutePath);
                }
            } catch (UnsatisfiedLinkError e) {
                throw new ResourceLoaderException("Failed to load the library using " + absolutePath, e);
            }
        }
    }


}
