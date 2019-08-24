/*
 * Copyright (c) Libly - Terl Tech Ltd  • 24/08/2019, 16:01 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.resourceloader;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class SharedLibraryLoader extends ResourceLoader {

    private final JnaLoader loader;
    private final Object lock = new Object();

    public SharedLibraryLoader(JnaLoader loader) {
        super();
        this.loader = loader;
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
        synchronized (lock) {
            try {
                File library = copyToTempDirectory(relativePath);
                setPermissions(library);
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
