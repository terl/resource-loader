/*
 * Copyright (c) Terl Tech Ltd  • 04/04/2021, 00:07 • goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.goterl.resourceloader;

import com.sun.jna.Platform;
import net.jodah.concurrentunit.Waiter;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SharedLibraryLoaderTest {

    @Test
    public void testLoadingFromJarOrFileSystem() {
        SharedLibraryLoader libLoader = SharedLibraryLoader.get();
        String relativePath = getLibraryPath();
        libLoader.load(relativePath, Sodium.class);
    }

    @Test
    public void testLoadingFromJarOrFileSystemParallel() throws TimeoutException, InterruptedException {
        SharedLibraryLoader libLoader = SharedLibraryLoader.get();
        String relativePath = getLibraryPath();
        final Waiter waiter = new Waiter();

        ExecutorService service = Executors.newFixedThreadPool(2);
        service.submit(() -> {
            libLoader.load(relativePath, Sodium.class);
            if (verifyLoaded()) {
                waiter.resume();
            }
        });
        service.submit(() -> {
            libLoader.load(relativePath, Sodium.class);
            if (verifyLoaded()) {
                waiter.resume();
            }
        });
        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);

        // Wait for resume() to be called twice
        waiter.await(2000, 2);
    }


    private boolean verifyLoaded() {
        Sodium sodium = new Sodium();
        sodium.sodium_init();

        byte[] message = "hello".getBytes();
        byte[] cipher = new byte[16 + message.length];
        byte[] nonce = new byte[24];
        byte[] key = new byte[32];

        sodium.crypto_secretbox_keygen(key);
        sodium.randombytes_buf(nonce, nonce.length);

        sodium.crypto_secretbox_easy(cipher, message, message.length, nonce, key);

        byte[] result = new byte[message.length];
        sodium.crypto_secretbox_open_easy(result, cipher, cipher.length, nonce, key);

        return Arrays.equals(result, message);
    }

    private String getLibraryPath() {
        if (Platform.isMac()) {
            return "shared_libraries/mac/libsodium.dylib";
        }
        if (Platform.isWindows()) {
            if (Platform.is64Bit()) {
                return "shared_libraries/windows64/libsodium.dll";
            } else {
                return "shared_libraries/windows/libsodium.dll";
            }
        }
        if (Platform.isARM()) {
            return "shared_libraries/armv6/libsodium.so";
        }
        if (Platform.isLinux()) {
            if (Platform.is64Bit()) {
                return "shared_libraries/linux64/libsodium.so";
            } else {
                return "shared_libraries/linux/libsodium.so";
            }
        }
        throw new UnsupportedOperationException("Platform not supported for testing.");
    }

}
