/*
 * Copyright (c) Libly - Terl Tech Ltd  • 24/08/2019, 16:01 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.resourceloader;

import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class SharedLibraryTest {

    private String sodiumPath = "shared_libraries/mac/libsodium.dylib";



//    @Test
//    public void testOneOffLoadingFromJar() {
//        JnaLoader jnaLoader = mock(JnaLoader.class);
//        SharedLibraryLoader libLoader = SharedLibraryLoader.get();
//        libLoader.load(sodiumPath, Sodium.class);
//        verifyLoadedFromJarOnce(jnaLoader);
//    }
//
//    @Test
//    public void testLoadingSystemWhenPresent() {
//        JnaLoader jnaLoader = mock(JnaLoader.class);
//        SharedLibraryLoader libLoader = SharedLibraryLoader.get();
//
//        // Try to load
//        libLoader.loadSystemLibrary("sodium", Sodium.class);
//
//        // Check the library was loaded
//        verifyLoadedOnce(jnaLoader, "sodium");
//    }


    private static void verifyLoadedOnce(JnaLoader jnaLoaderMock, String sodiumPath) {
        verify(jnaLoaderMock).register(Sodium.class, sodiumPath);
        verifyNoMoreInteractions(jnaLoaderMock);
    }

    private static void verifyLoadedFromJarOnce(JnaLoader jnaLoaderMock) {
        verify(jnaLoaderMock).register(eq(Sodium.class), anyString());
        verifyNoMoreInteractions(jnaLoaderMock);
    }

}
