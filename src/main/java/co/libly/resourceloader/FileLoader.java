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

    public File load(String relativePath) throws IOException {
        return load(relativePath, new HashSet<>());
    }

    public File load(String relativePath, Set<PosixFilePermission> permissions) throws IOException {
        return loadFromRelativePath(relativePath, permissions);
    }

    private File loadFromRelativePath(String relativePath, Set<PosixFilePermission> filePermissions) throws IOException {
        File file = copyToTempDirectory(relativePath);
        setPermissions(file, filePermissions);
        return file;
    }

}
