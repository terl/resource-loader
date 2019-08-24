package co.libly.resourceloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class FileLoader extends ResourceLoader {

    FileLoader() {
        super();
    }

    /**
     * Load a file/directory from your resource folder using a relative path.
     * This will return your file or directory with global read, write and execute.
     * @param relativePath Relative path to your file or directory.
     * @return The file your directory.
     * @throws IOException
     */
    public File load(String relativePath) throws IOException {
        return load(relativePath, new HashSet<>());
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
    public File load(String relativePath, Set<PosixFilePermission> permissions) throws IOException {
        return loadFromRelativePath(relativePath, permissions);
    }

    private File loadFromRelativePath(String relativePath, Set<PosixFilePermission> filePermissions) throws IOException {
        File file = copyToTempDirectory(relativePath);
        setPermissions(file, filePermissions);
        return file;
    }

}
