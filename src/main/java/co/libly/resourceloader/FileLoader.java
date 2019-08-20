package co.libly.resourceloader;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class FileLoader extends ResourceLoader {

    FileLoader() {
        super();
    }

    public File load(String relativePath) throws IOException, URISyntaxException {
        return load(relativePath, null, new HashSet<>());
    }

    public File load(String relativePath, String outputFolderName) throws IOException, URISyntaxException {
        return load(relativePath, outputFolderName, new HashSet<>());
    }

    public File load(String relativePath, String outputFolderName, Set<PosixFilePermission> permissions) throws IOException, URISyntaxException {
        if (loadedFiles.containsKey(relativePath)) {
            return loadedFiles.get(relativePath);
        } else {
            return loadFromRelativePath(relativePath, outputFolderName, permissions);
        }
    }

    private File loadFromRelativePath(String relativePath, String folderName, Set<PosixFilePermission> filePermissions) throws IOException, URISyntaxException {
        File file = copyFromJarToTemp(relativePath, folderName, filePermissions);
        return file;
    }

}
