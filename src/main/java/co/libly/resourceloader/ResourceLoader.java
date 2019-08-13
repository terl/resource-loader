package co.libly.resourceloader;


import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
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

    protected final Object lock = new Object();
    protected final ConcurrentHashMap<String, File> loadedFiles = new ConcurrentHashMap<>();

    ResourceLoader() {
    }

    public File copyFromJarToTemp(String pathInJar, String folderName, Set<PosixFilePermission> filePermissions) throws IOException {
        // If the file does not start with a separator,
        // then let's make sure it does!
        if (!pathInJar.startsWith(File.separator)) {
            pathInJar = File.separator + pathInJar;
        }

        // Create a "main" temporary directory in which
        // everything can be thrown in.
        File mainTempDir = createMainTempDirectory();

        // If the user wants to then put their files
        // in a subfolder, then so be it. Change
        // the main temp folder to be the new sub folder.
        if (folderName != null && !folderName.isEmpty()) {
            mainTempDir = new File(mainTempDir, folderName);
        }

        // Create the required directories.
        mainTempDir.mkdirs();

        File resource = new File(pathInJar);
        URL url = ResourceLoader.class.getResource(pathInJar);
        File f = new File(url.getPath());

        if (f.isFile()) {
            File resourceCopiedToTempFolder = new File(mainTempDir, resource.getName());
            FileUtils.copyFile(f, resourceCopiedToTempFolder);
            return resourceCopiedToTempFolder;
        } else {
            FileUtils.copyDirectory(f, mainTempDir);
            return mainTempDir;
        }
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

    public static File createMainTempDirectory() throws IOException {
        Path path = Files.createTempDirectory("resource-loader");
        File dir = path.toFile();
        dir.mkdir();
        dir.deleteOnExit();
        return dir;
    }

    private File setPermissions(File file, Set<PosixFilePermission> filePermissions) throws IOException {
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
        return file;
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
