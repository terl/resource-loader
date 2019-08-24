/*
 * Copyright (c) Libly - Terl Tech Ltd  • 24/08/2019, 16:01 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.resourceloader.mode;

public class ResourceLoaderMode {

    public static BundledMode createBundledMode(String relativePath) {
        return new BundledMode(relativePath);
    }

    public static SystemMode createSystemMode(String libraryName) {
        return new SystemMode(libraryName);
    }

}
