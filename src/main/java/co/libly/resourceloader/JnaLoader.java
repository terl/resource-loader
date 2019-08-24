/*
 * Copyright (c) Libly - Terl Tech Ltd  • 24/08/2019, 16:01 • libly.co, goterl.com
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.libly.resourceloader;

/**
 * A JNA loader, loading the library (if needed) and registering the class native
 * methods.
 *
 * <p>This interface exists to enable unit testing of library loading in a single
 * process — a thing that can only happen once.
 */
public interface JnaLoader {
    void register(Class<?> type, String libLocator);
}