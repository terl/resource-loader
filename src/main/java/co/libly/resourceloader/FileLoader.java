package co.libly.resourceloader;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class FileLoader extends ResourceLoader {

    FileLoader(JnaLoader loader) {
        super(loader);
    }

    public File load(String relativePath, String folderName) {
        return load(relativePath, folderName, new HashSet<>());
    }

    public File load(String relativePath, String folderName, Set<PosixFilePermission> permissions) {
        if (loadedFiles.containsKey(relativePath)) {
            return loadedFiles.get(relativePath);
        } else {
            return loadFromRelativePath(relativePath, folderName, permissions);
        }
    }

    private File loadFromRelativePath(String relativePath, String folderName, Set<PosixFilePermission> filePermissions) {
        File file = null;
        try {
            file = copyFromJarToTemp(relativePath, folderName, filePermissions);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
        return loadedFiles.put(relativePath, file);
    }

}
