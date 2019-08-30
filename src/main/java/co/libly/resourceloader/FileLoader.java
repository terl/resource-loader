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
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class FileLoader extends ResourceLoader {

    private FileLoader() {
        super();
    }

    /**
     * Get an instance of the loader.
     * @return Returns this loader instantiated.
     */
    public static FileLoader get() {
        return SingletonHelper.INSTANCE;
    }


    /**
     * Load a file/directory from your resource folder using a relative path.
     * This will return your file or directory with global read, write and execute.
     * @param relativePath Relative path to your file or directory.
     * @return The file your directory.
     * @throws IOException
     */
    public File load(String relativePath, Class outsideClass) throws IOException {
        return load(relativePath, new HashSet<>(), outsideClass);
    }

    /**
     * Load a file/directory from your resource folder with permissions
     * you set. On windows, any type of read, write and execute permissions will
     * be set appropriately.
     * @param relativePath Relative path to your file or directory.
     * @param permissions A set of permissions.
     * @return The file your directory.
     * @throws IOException
     */
    public File load(String relativePath, Set<PosixFilePermission> permissions, Class outsideClass) throws IOException {
        return loadFromRelativePath(relativePath, permissions, outsideClass);
    }

    private File loadFromRelativePath(String relativePath, Set<PosixFilePermission> filePermissions, Class outsideClass) throws IOException {
        File file = copyToTempDirectory(relativePath, outsideClass);
        setPermissions(file, filePermissions);
        return file;
    }

    private static class SingletonHelper {
        private static final FileLoader INSTANCE = new FileLoader();
    }
}
