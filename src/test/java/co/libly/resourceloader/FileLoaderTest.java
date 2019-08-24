package co.libly.resourceloader;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.*;

public class FileLoaderTest {

    @Test
    public void loadFile() throws IOException, URISyntaxException {
        FileLoader fileLoader = new FileLoader();
        File file = fileLoader.load("test1.txt");

        assertThat(file)
                .as("Load a file")
                .isNotNull();
    }

    @Test
    public void loadFileCheckContents() throws IOException, URISyntaxException {
        FileLoader fileLoader = new FileLoader();
        File file = fileLoader.load("test1.txt");

        assertThat(file)
                .as("Load a file testing the content")
                .hasContent("this is a test");
    }

    @Test
    public void loadWholeFolders() throws IOException, URISyntaxException {
        FileLoader fileLoader = new FileLoader();
        File file = fileLoader.load("folder2");

        assertThat(file)
                .as("Load a directory with children")
                .isDirectory()
                .isNotEmptyDirectory();
    }

}
