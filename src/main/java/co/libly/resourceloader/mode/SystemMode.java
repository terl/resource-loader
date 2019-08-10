package co.libly.resourceloader.mode;

public class SystemMode extends ResourceLoaderMode {

    private String libraryName;

    public SystemMode(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    @Override
    public String toString() {
        return getLibraryName();
    }

}
