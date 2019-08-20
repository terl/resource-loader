package co.libly.resourceloader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        System.out.println("Running.");
        FileLoader fileLoader = new FileLoader();
        try {
            File f = fileLoader.load("file1.txt");
            if (f.isFile()) {
                List<String> content = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
                System.out.println(content.get(0));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }


}
