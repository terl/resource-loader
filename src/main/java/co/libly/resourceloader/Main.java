/*
 * Copyright (c) Libly - Terl Tech Ltd  • 24/08/2019, 16:01 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.resourceloader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        System.out.println("Running Resource Loader.");
        FileLoader fileLoader = new FileLoader();
        try {
            File f = fileLoader.load("file1.txt");
            if (f.isFile()) {
                List<String> content = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
                System.out.println(content.get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
