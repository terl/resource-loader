/*
 * Copyright (c) Libly - Terl Tech Ltd  • 28/01/2021, 21:08 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.resourceloader;

// A test Sodium class
public class Sodium {

    protected Sodium() { }


    //// -------------------------------------------|
    //// HELPERS
    //// -------------------------------------------|


    public native int sodium_init();
    public native void sodium_increment(byte[] n, int nLen);
    public native void sodium_add(byte[] a, byte[] b, int len);
    public native int sodium_is_zero(byte[] n, int nLen);
    public native void sodium_stackzero(int len);
    public native int sodium_memcmp(byte[] b1, byte[] b2, int len);
    public native int sodium_base64_encoded_len(int binLen, int variant);
    public native int sodium_compare(byte[] b1, byte[] b2, int len);


    public native String sodium_bin2hex(byte[] hex, int hexMaxLen, byte[] bin, int binLen);

    public native int sodium_hex2bin(byte[] bin,
                                     int binMaxLen,
                                     byte[] hex,
                                     int hexLen,
                                     byte[] ignore,
                                     int binLen,
                                     byte hexEnd);

    public native String sodium_bin2base64(byte[] b64,
                                          int b64MaxLen,
                                          byte[] bin,
                                          int binLen,
                                          int variant);

    public native int sodium_base642bin(byte[] bin,
                                        int binMaxLen,
                                        byte[] b64,
                                        int b64Len,
                                        byte[] ignore,
                                        int binLen,
                                        byte b64End,
                                        int variant);


    //// -------------------------------------------|
    //// PADDING
    //// -------------------------------------------|

    public native int sodium_pad(int paddedBuffLen, char[] buf, int unpaddedBufLen, int blockSize, int maxBufLen);

    public native int sodium_unpad(int paddedBuffLen, char[] buf, int unpaddedBufLen, int blockSize);




    //// -------------------------------------------|
    //// RANDOM
    //// -------------------------------------------|

    public native byte randombytes_random();

    public native byte randombytes_uniform(int upperBound);

    public native void randombytes_buf(byte[] buffer, int size);

    public native void randombytes_buf_deterministic(byte[] buffer, int size, byte[] seed);


    //// -------------------------------------------|
    //// SECRET BOX
    //// -------------------------------------------|

    public native void crypto_secretbox_keygen(byte[] key);


    public native int crypto_secretbox_easy(byte[] cipherText,
                                     byte[] message,
                                     long messageLen,
                                     byte[] nonce,
                                     byte[] key);

    public native int crypto_secretbox_open_easy(byte[] message,
                                          byte[] cipherText,
                                          long cipherTextLen,
                                          byte[] nonce,
                                          byte[] key);

    public native int crypto_secretbox_detached(byte[] cipherText,
                                         byte[] mac,
                                         byte[] message,
                                         long messageLen,
                                         byte[] nonce,
                                         byte[] key);

    public native int crypto_secretbox_open_detached(byte[] message,
                                              byte[] cipherText,
                                              byte[] mac,
                                              long cipherTextLen,
                                              byte[] nonce,
                                              byte[] key);



    //// -------------------------------------------|
    //// CRYPTO BOX
    //// -------------------------------------------|

    public native int crypto_box_keypair(byte[] publicKey, byte[] secretKey);

    public native int crypto_box_seed_keypair(byte[] publicKey, byte[] secretKey, byte[] seed);


    public native int crypto_box_easy(
        byte[] cipherText,
        byte[] message,
        long messageLen,
        byte[] nonce,
        byte[] publicKey,
        byte[] secretKey
    );

    public native int crypto_box_open_easy(
            byte[] message,
            byte[] cipherText,
            long cipherTextLen,
            byte[] nonce,
            byte[] publicKey,
            byte[] secretKey
    );

    public native int crypto_box_detached(byte[] cipherText,
                                   byte[] mac,
                                   byte[] message,
                                   long messageLen,
                                   byte[] nonce,
                                   byte[] publicKey,
                                   byte[] secretKey);

    public native int crypto_box_open_detached(byte[] message,
                                        byte[] cipherText,
                                        byte[] mac,
                                        byte[] cipherTextLen,
                                        byte[] nonce,
                                        byte[] publicKey,
                                        byte[] secretKey);

    public native int crypto_box_beforenm(byte[] k, byte[] publicKey, byte[] secretKey);


    public native int crypto_box_easy_afternm(
        byte[] cipherText,
        byte[] message,
        long messageLen,
        byte[] nonce,
        byte[] key
    );

    public native int crypto_box_open_easy_afternm(
            byte[] message, byte[] cipher,
            long cLen, byte[] nonce,
            byte[] key
    );

    public native int crypto_box_detached_afternm(
            byte[] cipherText,
            byte[] mac,
            byte[] message,
            long messageLen,
            byte[] nonce,
            byte[] key
    );

    public native int crypto_box_open_detached_afternm(byte[] message,
                                        byte[] cipherText,
                                        byte[] mac,
                                        long cipherTextLen,
                                        byte[] nonce,
                                        byte[] key);


    public native int crypto_box_seal(byte[] cipher, byte[] message, long messageLen, byte[] publicKey);

    public native int crypto_box_seal_open(byte[] m,
                                    byte[] cipher,
                                    long cipherLen,
                                    byte[] publicKey,
                                    byte[] secretKey);

}
