package co.libly.resourceloader;


import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Loads resources from a relative or absolute path
 * even if the file is in a JAR.
 */
public class ResourceLoader {

    protected final JnaLoader loader;
    protected final Object lock = new Object();
    protected final ConcurrentHashMap<String, File> loadedFiles = new ConcurrentHashMap<>();

    // VisibleForTesting
    ResourceLoader(JnaLoader loader) {
        this.loader = loader;
    }

    public File copyFromJarToTemp(String pathInJar, String folderName, Set<PosixFilePermission> filePermissions) throws IOException {
        if (!pathInJar.startsWith(File.separator)) {
            pathInJar = File.separator + pathInJar;
        }

        File tempFolder = createMainTempDirectory();
        if (folderName != null && !folderName.isEmpty()) {
            tempFolder = new File(tempFolder, folderName);
        }

        File fileInJar = new File(pathInJar);
        File fileInTempFolder = new File(tempFolder, fileInJar.getName());
        fileInTempFolder.createNewFile();

        InputStream is = ResourceLoader.class.getResourceAsStream(pathInJar);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(fileInTempFolder, false));

        try {
            copy(is, out);
        } catch (Exception e) {
            fileInTempFolder.delete();
            throw new IOException(e);
        }

        setPermissions(fileInTempFolder, filePermissions);

        return fileInTempFolder;
    }

    private static void copy(InputStream is, OutputStream out) throws IOException {
        try {
            byte[] dest = new byte[4096];
            int amt = is.read(dest);
            while (amt != -1) {
                out.write(dest, 0, amt);
                amt = is.read(dest);
            }
        } finally {
            is.close();
            out.close();
        }
    }

    // VisibleForTesting
    static File createMainTempDirectory() throws IOException {
        Path path = Files.createTempDirectory("resource-loader");
        File dir = path.toFile();
        dir.mkdir();
        dir.deleteOnExit();
        return dir;
    }

    private void setPermissions(File file, Set<PosixFilePermission> filePermissions) throws IOException{
        if (isPosixCompliant()) {
            if (filePermissions.isEmpty()) {
                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.OWNER_WRITE);
                perms.add(PosixFilePermission.OWNER_EXECUTE);

                perms.add(PosixFilePermission.OTHERS_READ);
                perms.add(PosixFilePermission.OTHERS_WRITE);
                perms.add(PosixFilePermission.OTHERS_EXECUTE);

                perms.add(PosixFilePermission.GROUP_READ);
                perms.add(PosixFilePermission.GROUP_WRITE);
                perms.add(PosixFilePermission.GROUP_EXECUTE);
                filePermissions = perms;
            }
            Files.setPosixFilePermissions(file.toPath(), filePermissions);
        } else {
            file.setWritable(true);
            file.setReadable(true);
            file.setExecutable(true);
        }
    }

    protected void requestDeletion(File file) {
        if (isPosixCompliant()) {
            // The file can be deleted immediately after loading
            file.delete();
        } else {
            // Don't delete until last file descriptor closed
            file.deleteOnExit();
        }
    }

    protected boolean isPosixCompliant() {
        try {
            return FileSystems.getDefault()
                    .supportedFileAttributeViews()
                    .contains("posix");
        } catch (FileSystemNotFoundException | ProviderNotFoundException | SecurityException e) {
            return false;
        }
    }
}
