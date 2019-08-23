package co.libly.resourceloader;

import org.mockito.AdditionalMatchers;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

public class SharedLibraryTest {

    String sodiumPath = "shared_libraries/linux/libsodium.so";


    @Test
    public void testOneOffLoadingFromJar() {
        JnaLoader jnaLoader = mock(JnaLoader.class);
        SharedLibraryLoader libLoader = new SharedLibraryLoader(jnaLoader);

        libLoader.loadBundledLibrary(sodiumPath, Sodium.class);

        verifyLoadedFromJarOnce(jnaLoader);
    }

    @Test
    public void testLoadingSystemWhenPresent() {
        JnaLoader jnaLoader = mock(JnaLoader.class);
        SharedLibraryLoader libLoader = new SharedLibraryLoader(jnaLoader);

        // Try to load
        libLoader.loadSystemLibrary("sodium", Sodium.class);

        // Check the library was loaded
        verifyLoadedOnce(jnaLoader, "sodium");
    }


    private static void verifyLoadedOnce(JnaLoader jnaLoaderMock, String sodiumPath) {
        verify(jnaLoaderMock).register(Sodium.class, sodiumPath);
        verifyNoMoreInteractions(jnaLoaderMock);
    }

    private static void verifyLoadedFromJarOnce(JnaLoader jnaLoaderMock) {
        verify(jnaLoaderMock).register(eq(Sodium.class), anyString());
        verifyNoMoreInteractions(jnaLoaderMock);
    }

}
