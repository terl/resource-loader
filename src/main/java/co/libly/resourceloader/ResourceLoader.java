package co.libly.resourceloader;


import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Loads resources from a relative or absolute path
 * even if the file is in a JAR.
 */
public class ResourceLoader {

    protected final Object lock = new Object();
    protected final ConcurrentHashMap<String, File> loadedFiles = new ConcurrentHashMap<>();

    ResourceLoader() { }

    public File copyFromJarToTemp(String pathInJar,
                                  String outputFolderName,
                                  Set<PosixFilePermission> filePermissions) throws IOException {
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
        if (outputFolderName != null && !outputFolderName.isEmpty()) {
            mainTempDir = new File(mainTempDir, outputFolderName);
        }

        // Create the required directories.
        mainTempDir.mkdirs();

        try {
            URL jarUrl = getThisJarPath();
            // Is the user loading this in a JAR?
            if (jarUrl.toString().endsWith(".jar")) {
                // If so the get the file/directory
                // from a JAR
                return getFileFromJar(jarUrl, mainTempDir, pathInJar);
            } else {
                // If not then get the file/directory
                // straight from the file system
                return getFileFromFileSystem(pathInJar, mainTempDir);
            }
        } catch (URISyntaxException e) {
            // If we could not convert the jarUrl to a URI
            // then it means we might not have a JAR,
            // so we try load from the file system.
            return getFileFromFileSystem(pathInJar, mainTempDir);
        }
    }

    private File getFileFromJar(URL jarUrl, File mainTempDir, String pathInJar) throws URISyntaxException, IOException {
        File jar = new File(jarUrl.toURI());
        unzip(jar.getAbsolutePath(), mainTempDir.getAbsolutePath());
        String filePath = mainTempDir.getAbsolutePath() + pathInJar;
        return new File(filePath);
    }

    private File getFileFromFileSystem(String pathInJar, File mainTempDir) throws IOException {
        final URL url = ResourceLoader.class.getResource(pathInJar);
        final String urlString = url.getFile();
        final File file = new File(urlString);

        if (file.isFile()) {
            File resource = new File(pathInJar);
            File resourceCopiedToTempFolder = new File(mainTempDir, resource.getName());
            doCopyFile(file, resourceCopiedToTempFolder);
            return resourceCopiedToTempFolder;
        } else {
            copyDirectory(file, mainTempDir);
            return mainTempDir;
        }
    }

    /**
     * From https://www.javadevjournal.com/java/zipping-and-unzipping-in-java/
     * @param zipFilePath An absolute path to a zip file
     * @param unzipLocation Where to unzip the zip file
     * @throws IOException If could not unzip
     */
    private static void unzip(final String zipFilePath, final String unzipLocation) throws IOException {
        if (!(Files.exists(Paths.get(unzipLocation)))) {
            Files.createDirectories(Paths.get(unzipLocation));
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                Path filePath = Paths.get(unzipLocation, entry.getName());
                if (!entry.isDirectory()) {
                    unzipFiles(zipInputStream, filePath);
                } else {
                    Files.createDirectories(filePath);
                }

                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        }
    }

    private static void unzipFiles(final ZipInputStream zipInputStream, final Path unzipFilePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzipFilePath.toAbsolutePath().toString()))) {
            byte[] bytesIn = new byte[1024];
            int read = 0;
            while ((read = zipInputStream.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }


    private static final long FILE_COPY_BUFFER_SIZE = 1000000 * 30;

    /**
     * From Apache Commons
     * @param srcFile
     * @param destFile
     * @throws IOException
     */
    private static void doCopyFile(final File srcFile, final File destFile)
            throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        try (FileInputStream fis = new FileInputStream(srcFile);
             FileChannel input = fis.getChannel();
             FileOutputStream fos = new FileOutputStream(destFile);
             FileChannel output = fos.getChannel()) {
            final long size = input.size(); // TODO See IO-386
            long pos = 0;
            long count = 0;
            while (pos < size) {
                final long remain = size - pos;
                count = remain > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : remain;
                final long bytesCopied = output.transferFrom(input, pos, count);
                if (bytesCopied == 0) { // IO-385 - can happen if file is truncated after caching the size
                    break; // ensure we don't loop forever
                }
                pos += bytesCopied;
            }
        }

        final long srcLen = srcFile.length(); // TODO See IO-386
        final long dstLen = destFile.length(); // TODO See IO-386
        if (srcLen != dstLen) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "' Expected length: " + srcLen + " Actual: " + dstLen);
        }
    }

    /**
     * From Apache Commons
     * @param srcDir
     * @param destDir
     * @throws IOException
     */
    private static void copyDirectory(final File srcDir, final File destDir) throws IOException {
        if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        }

        // Cater for destination being directory within the source directory (see IO-141)
        List<String> exclusionList = null;
        if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
            final File[] srcFiles = srcDir.listFiles();
            if (srcFiles != null && srcFiles.length > 0) {
                exclusionList = new ArrayList<>(srcFiles.length);
                for (final File srcFile : srcFiles) {
                    final File copiedFile = new File(destDir, srcFile.getName());
                    exclusionList.add(copiedFile.getCanonicalPath());
                }
            }
        }
        doCopyDirectory(srcDir, destDir, exclusionList);
    }

    private static void doCopyDirectory(final File srcDir, final File destDir, final List<String> exclusionList)
            throws IOException {
        // recurse
        final File[] srcFiles = srcDir.listFiles();
        if (srcFiles == null) {  // null if abstract pathname does not denote a directory, or if an I/O error occurs
            throw new IOException("Failed to list contents of " + srcDir);
        }
        if (destDir.exists()) {
            if (destDir.isDirectory() == false) {
                throw new IOException("Destination '" + destDir + "' exists but is not a directory");
            }
        } else {
            if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }
        }
        if (destDir.canWrite() == false) {
            throw new IOException("Destination '" + destDir + "' cannot be written to");
        }
        for (final File srcFile : srcFiles) {
            final File dstFile = new File(destDir, srcFile.getName());
            if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                if (srcFile.isDirectory()) {
                    doCopyDirectory(srcFile, dstFile, exclusionList);
                } else {
                    doCopyFile(srcFile, dstFile);
                }
            }
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

    public URL getThisJarPath() {
        return getClass().getProtectionDomain().getCodeSource().getLocation();
    }
}
