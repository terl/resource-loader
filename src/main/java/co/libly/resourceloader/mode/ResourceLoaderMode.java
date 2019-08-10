package co.libly.resourceloader.mode;

public class ResourceLoaderMode {

    public static BundledMode createBundledMode(String relativePath) {
        return new BundledMode(relativePath);
    }

    public static SystemMode createSystemMode(String libraryName) {
        return new SystemMode(libraryName);
    }

}
