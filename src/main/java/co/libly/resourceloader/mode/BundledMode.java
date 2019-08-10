package co.libly.resourceloader.mode;

public class BundledMode extends ResourceLoaderMode {

    private String relativePath;

    public BundledMode(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public String toString() {
        return getRelativePath();
    }

}
