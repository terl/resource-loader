package com.goterl.resourceloader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceLoaderTest {
    File tmpDir;

    @BeforeTest
    public void before() {
        String resourcePath = ResourceLoaderTest.class.getResource("/").getPath();
        tmpDir = new File(resourcePath + "/tmp");
        if (!tmpDir.exists()) {
            assertThat(tmpDir.mkdirs()).isTrue();
        }
    }

    @AfterTest
    public void after() {
        if (tmpDir.exists()) {
            delete(tmpDir.getPath());
        }
    }

    @DataProvider(name = "jarUrlToFileUrlTestData")
    public static Object[][] jarUrls() {
        return new Object[][] {
                {
                    "file:/app/.m2/repository/com/goterl/lazysodium-java/5.0.1/lazysodium-java-5.0.1.jar",
                        "file:/app/.m2/repository/com/goterl/lazysodium-java/5.0.1/lazysodium-java-5.0.1.jar"
                },
                {
                    "jar:file:/app/.m2/repository/com/goterl/lazysodium-java/5.0.1/lazysodium-java-5.0.1.jar!/",
                        "file:/app/.m2/repository/com/goterl/lazysodium-java/5.0.1/lazysodium-java-5.0.1.jar"
                },
                {
                        "jar:file:/app/target/app-0.0.1-SNAPSHOT.jar!/BOOT-INF/lib/lazysodium-java-5.0.1.jar!/",
                        "file:/app/target/app-0.0.1-SNAPSHOT.jar/BOOT-INF/lib/lazysodium-java-5.0.1.jar"
                }
        };
    }

    @Test(dataProvider = "jarUrlToFileUrlTestData")
    public void jarUrlToFileUrlTest(String url, String expect) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        Method method = ResourceLoader.class.getDeclaredMethod("getPathToTheNestedJar", String.class);
        method.setAccessible(true);
        Object result = method.invoke(null, url);
        assertThat(result).isEqualTo(new URL(expect));
    }

    @DataProvider(name = "isJarFileTestData")
    public static Object[][] isjars() {
        String jarUrl = ResourceLoaderTest.class.getResource("/jarinjar.jar").toString();
        String jarUrlWithSpaces = ResourceLoaderTest.class.getResource("/jar with spaces.jar").toString();
        String innerJarUrl = jarUrl + "/lazysodium.jar";
        return new Object[][] {
          {
            jarUrl,
            true
          },
          {
            jarUrlWithSpaces,
            true
          },
          {
            innerJarUrl,
            true
          }
        };
    }

    @Test(dataProvider = "isJarFileTestData")
    public void isJarFileTest(String url, Boolean isJar) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        Method method = ResourceLoader.class.getDeclaredMethod("isJarFile", URL.class);
        method.setAccessible(true);
        Object result = method.invoke(new ResourceLoader(), new URL(url));
        assertThat(result).isEqualTo(isJar);
    }

    @DataProvider(name = "nestedExtractTestData")
    public static Object[][] nestedExtractTestData() {
        String jarUrl = ResourceLoaderTest.class.getResource("/jarinjar.jar").toString();
        return new Object[][] {
                {
                    jarUrl,
                        "com/goterl/lazycode/lazysodium/LazySodiumJava.java"
                },
                {
                    jarUrl + "/lazysodium.jar",
                        "com/goterl/lazycode/lazysodium/LazySodiumJava.class"
                }
        };
    }

    @Test(dataProvider = "nestedExtractTestData")
    public void nestedExtractTest(String url, String path) throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException, URISyntaxException {

        String dirName = Files.createTempDirectory("resource-loader").getFileName().toString();
        File dir = new File(tmpDir.getPath() + "/" + dirName);
        dir.mkdir();

        ResourceLoader loader = new ResourceLoader();
        File file = loader.extractFromWithinAJarFile(new URL(url), dir, path);

        assertThat(file).isNotNull();
        assertThat(file.exists()).isTrue();
    }

    private static boolean delete(String path) {
        File filePath = new File(path);
        String[] list = filePath.list();
        for (String file : list) {
            File f = new File(path + File.separator + file);
            boolean result;
            if (f.isDirectory()) {
                result = delete(path + File.separator + file);
            } else {
                result = f.delete();
            }
            if (result == false) {
                return false;
            }
        }
        return filePath.delete();
    }
}
