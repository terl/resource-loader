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
        File file = fileLoader.load("test1.txt", "output");

        assertThat(file)
                .as("Load a file")
                .isNotNull();
    }

    @Test
    public void loadFileCheckContents() throws IOException, URISyntaxException {
        FileLoader fileLoader = new FileLoader();
        File file = fileLoader.load("test1.txt", "output");

        assertThat(file)
                .as("Load a file testing the content")
                .hasContent("this is a test");
    }

    @Test
    public void loadFileInAFolder() throws IOException, URISyntaxException {
        FileLoader fileLoader = new FileLoader();
        File file = fileLoader.load("folder/test2.txt", "output");

        assertThat(file)
                .as("Load a file in a folder testing the content")
                .hasContent("test file 2");
    }

    @Test
    public void loadWholeFolders() throws IOException, URISyntaxException {
        FileLoader fileLoader = new FileLoader();
        File file = fileLoader.load("folder2", "folder-output");

        assertThat(file)
                .as("Load a directory with children")
                .isDirectory()
                .isNotEmptyDirectory();
    }

}
