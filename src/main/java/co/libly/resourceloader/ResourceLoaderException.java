/*
 * Copyright (c) Libly - Terl Tech Ltd  • 28/01/2021, 21:08 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.resourceloader;

/**
 * Indicates a failure to load the required library.
 */
public class ResourceLoaderException extends RuntimeException {

    public ResourceLoaderException(String message) {
        super(message);
    }

    public ResourceLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
