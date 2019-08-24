/*
 * Copyright (c) Libly - Terl Tech Ltd  • 24/08/2019, 16:01 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.resourceloader;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.*;

public class FileLoaderTest {

    private FileLoader fileLoader;

    @BeforeClass
    public void loadLoader() {
        fileLoader = FileLoader.get();
    }

    @Test
    public void loadFile() throws IOException, URISyntaxException {
        File file = fileLoader.load("test1.txt");
        assertThat(file)
                .as("Load a file")
                .isNotNull();
    }

    @Test
    public void loadFileCheckContents() throws IOException, URISyntaxException {
        File file = fileLoader.load("test1.txt");

        assertThat(file)
                .as("Load a file testing the content")
                .hasContent("this is a test");
    }

    @Test
    public void loadWholeFolders() throws IOException, URISyntaxException {
        File file = fileLoader.load("folder2");

        assertThat(file)
                .as("Load a directory with children")
                .isDirectory()
                .isNotEmptyDirectory();
    }

}
