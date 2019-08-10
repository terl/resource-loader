package co.libly.resourceloader;

import co.libly.resourceloader.mode.BundledMode;
import co.libly.resourceloader.mode.ResourceLoaderMode;
import co.libly.resourceloader.mode.SystemMode;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SharedLibraryLoader extends ResourceLoader {

    SharedLibraryLoader(JnaLoader loader) {
        super(loader);
    }

    public boolean load(List<ResourceLoaderMode> modes, List<Class> classesToBind) {
        if (modes.size() == 0) {
            return false;
        }
        int errorCount = 0;
        synchronized (lock) {
            for (int i = 0; i < modes.size(); i++) {
                ResourceLoaderMode mode = modes.get(i);
                if (mode instanceof SystemMode) {
                    try {
                        SystemMode systemMode = (SystemMode) mode;
                        loadSystemLibrary(systemMode.getLibraryName(), classesToBind);
                    } catch (Exception e) {
                        String error = String.format("Try %s loading from system:\n", i);
                        IOException newError = new IOException(error);
                        newError.printStackTrace();
                        errorCount++;
                    }
                }
                if (mode instanceof BundledMode) {
                    try {
                        BundledMode bundledMode = (BundledMode) mode;
                        loadBundledLibrary(bundledMode.getRelativePath(), classesToBind);
                    } catch (Exception e) {
                        String error = String.format("Try %s loading from bundle:\n", i);
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
            return false;
        }

        return true;
    }

    private void loadSystemLibrary(String libraryName, List<Class> classes) {
        loadLibrary(libraryName, classes);
    }

    private void loadBundledLibrary(String relativePath, List<Class> classes) {
        try {
            File library = copyFromJarToTemp(relativePath, null, new HashSet<>());
            loadLibrary(library.getAbsolutePath(), classes);
            requestDeletion(library);
        } catch (IOException e) {
            String message = String.format(
                    "Failed to load the bundled library from resources by relative path (%s)",
                    relativePath
            );
            throw new ResourceLoaderException(message, e);
        }
    }

    private void loadLibrary(String absolutePath, List<Class> classes) {
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
